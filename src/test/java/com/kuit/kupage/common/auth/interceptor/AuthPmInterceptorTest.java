package com.kuit.kupage.common.auth.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuit.kupage.common.auth.AuthMember;
import com.kuit.kupage.domain.memberRole.MemberRole;
import com.kuit.kupage.domain.memberRole.service.MemberRoleService;
import com.kuit.kupage.domain.role.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

import static com.kuit.kupage.common.response.ResponseCode.FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthPmInterceptorTest {

    @Mock
    private MemberRoleService memberRoleService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Authentication authentication;

    @Mock
    private AuthMember authMember;

    private AuthPmInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new AuthPmInterceptor(memberRoleService);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(authMember);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @DisplayName("관리자이면 true를 리턴하고 403을 설정하지 않는다")
    @Test
    void admin_should_return_true_and_not_set_403() throws Exception {
        // given
        when(authMember.isAdmin()).thenReturn(true);
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        boolean result = interceptor.preHandle(request, response, new Object());

        // then
        assertThat(result).isTrue();
        assertThat(response.getStatus()).isNotEqualTo(SC_FORBIDDEN);
        assertThat(response.getContentAsString()).isEmpty();

        // PM 체크 로직은 호출되지 않아야 함
        verify(memberRoleService, never()).getMemberRolesByMemberId(anyLong());
    }


    @DisplayName("PM 권한이면 true를 리턴하고 403을 설정하지 않는다")
    @Test
    void pm_role_should_return_true_and_not_set_403() throws Exception {
        // given
        when(authMember.isAdmin()).thenReturn(false);
        when(authMember.getId()).thenReturn(1L);

        Role role = mock(Role.class);
        when(role.getName()).thenReturn("PM"); // contains("PM") 조건 만족

        MemberRole memberRole = mock(MemberRole.class);
        when(memberRole.getRole()).thenReturn(role);

        when(memberRoleService.getMemberRolesByMemberId(1L))
                .thenReturn(List.of(memberRole));

        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        boolean result = interceptor.preHandle(request, response, new Object());

        // then
        assertThat(result).isTrue();
        assertThat(response.getStatus()).isNotEqualTo(SC_FORBIDDEN);
        assertThat(response.getContentAsString()).isEmpty();
    }

    @DisplayName("PM이 아니면 false를 리턴하고 403과 JSON을 응답한다")
    @Test
    void non_pm_should_return_false_and_respond_403_with_json() throws Exception {
        // given
        when(authMember.isAdmin()).thenReturn(false);
        when(authMember.getId()).thenReturn(1L);
        when(memberRoleService.getMemberRolesByMemberId(1L))
                .thenReturn(Collections.emptyList()); // PM 없음

        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        boolean result = interceptor.preHandle(request, response, new Object());

        // then
        assertThat(result).isFalse();
        assertThat(response.getStatus()).isEqualTo(SC_FORBIDDEN);
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(response.getContentAsString()).isNotBlank();

        // 필요하면 응답 내용까지 검증 가능
        String expectedJson = new ObjectMapper().writeValueAsString(FORBIDDEN);
        assertThat(response.getContentAsString()).isEqualTo(expectedJson);
    }
}