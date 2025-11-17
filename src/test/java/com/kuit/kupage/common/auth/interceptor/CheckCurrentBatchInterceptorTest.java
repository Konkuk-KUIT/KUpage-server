package com.kuit.kupage.common.auth.interceptor;

import com.kuit.kupage.common.auth.AuthMember;
import com.kuit.kupage.common.constant.ConstantProperties;
import com.kuit.kupage.domain.memberRole.service.MemberRoleService;
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

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckCurrentBatchInterceptorTest {

    @Mock
    private MemberRoleService memberRoleService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Authentication authentication;

    @Mock
    private AuthMember authMember;

    @Mock
    private ConstantProperties constantProperties;
    private CheckCurrentBatchInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new CheckCurrentBatchInterceptor(memberRoleService, constantProperties);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(authMember);
        when(authMember.getId()).thenReturn(1L);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @DisplayName("현재 기수이면 true를 리턴하고 403을 설정하지 않는다")
    @Test
    void return_true_when_current_batch_and_not_set_403() throws Exception {
        // given
        when(memberRoleService.isCurrentBatch(1L)).thenReturn(true);
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        boolean result = interceptor.preHandle(request, response, new Object());

        // then
        assertThat(result).isTrue();
        assertThat(response.getStatus()).isNotEqualTo(SC_FORBIDDEN);
        assertThat(response.getContentAsString()).isEmpty();
    }

    @DisplayName("현재 기수가 아니면 false를 리턴하고 403과 JSON을 응답한다")
    @Test
    void return_false_when_not_current_batch_and_respond_403_with_json() throws Exception {
        // given
        when(memberRoleService.isCurrentBatch(1L)).thenReturn(false);
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        boolean result = interceptor.preHandle(request, response, new Object());

        // then
        assertThat(result).isFalse();
        assertThat(response.getStatus()).isEqualTo(SC_FORBIDDEN);
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(response.getContentAsString()).isNotBlank(); // NOT_CURRENT_BATCH_MEMBER가 JSON으로 들어가 있음
    }
}