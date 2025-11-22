package com.kuit.kupage.domain.teamMatch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuit.kupage.common.auth.AuthMember;
import com.kuit.kupage.common.auth.interceptor.AuthAllowedPartInterceptor;
import com.kuit.kupage.common.auth.interceptor.CheckCurrentBatchInterceptor;
import com.kuit.kupage.common.auth.interceptor.InjectionRoleInterceptor;
import com.kuit.kupage.common.config.InterceptorConfig;
import com.kuit.kupage.common.config.SecurityTestConfig;
import com.kuit.kupage.domain.memberRole.service.MemberRoleService;
import com.kuit.kupage.domain.teamMatch.Part;
import com.kuit.kupage.domain.teamMatch.dto.TeamApplicantOverviewDto;
import com.kuit.kupage.domain.teamMatch.dto.TeamApplicantResponse;
import com.kuit.kupage.domain.teamMatch.dto.TeamMatchRequest;
import com.kuit.kupage.domain.teamMatch.dto.TeamMatchResponse;
import com.kuit.kupage.domain.teamMatch.service.TeamMatchService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TeamMatchController.class)
@Import({SecurityTestConfig.class, InterceptorConfig.class})
public class TeamMatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeamMatchService teamMatchService;

    @MockitoBean
    private MemberRoleService memberRoleService;
    @MockitoBean
    private AuthAllowedPartInterceptor authAllowedPartInterceptor;
    //    @MockitoBean
//    private InjectionRoleInterceptor injectionRoleInterceptor;
    @MockitoBean
    private CheckCurrentBatchInterceptor checkCurrentBatchInterceptor;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        // 1. AuthPmInterceptor의 preHandle이 항상 true를 반환하도록 설정
        //    (즉, 인증/인가 단계를 통과하도록 함)
        when(authAllowedPartInterceptor.preHandle(
                any(), any(), any()
        )).thenReturn(true);

        // 2. CheckCurrentBatchInterceptor의 preHandle도 항상 true를 반환하도록 설정
        //    (즉, 현재 기수 체크 단계를 통과하도록 함)
        when(checkCurrentBatchInterceptor.preHandle(
                any(), any(), any()
        )).thenReturn(true);
    }

    @Test
    @DisplayName("팀 매치 지원 요청이 성공적으로 처리되는지 테스트")
    public void apply_should_success_when_valid_request() throws Exception {
        // given
        TeamMatchResponse mockResponse = new TeamMatchResponse(1L);
        when(teamMatchService.apply(anyLong(), anyLong(), any(TeamMatchRequest.class))).thenReturn(mockResponse);

        TeamMatchRequest request = new TeamMatchRequest(Part.Android, "팀의 안드로이드 앱 개발을 담당하고 싶습니다.", "https://portfolio.example.com/seoyeon");

        AuthMember authMember = new AuthMember(1L, List.of(new SimpleGrantedAuthority("ROLE_MEMBER")));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(authMember, null, authMember.getAuthorities());

        // when
        mockMvc.perform(post("/teams/1/match").with(authentication(authenticationToken)).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                // then
                .andExpect(status().isOk()).andExpect(jsonPath("$.result.teamApplicantId").value(1));
    }

    @Test
    @DisplayName("관리자일 경우 모든 현재 기수 팀 지원 현황을 반환한다")
    public void application_status_should_return_all_current_batch_applicants_for_admin() throws Exception {
        // given
        AuthMember adminMember = new AuthMember(1L, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(adminMember, null, adminMember.getAuthorities());

        when(teamMatchService.getAllCurrentBatchTeamApplicants())
                .thenReturn(List.of()); // 비어 있는 리스트라도 응답 형태만 검증

        // when & then
        mockMvc.perform(get("/teams/applications")
                        .with(authentication(authenticationToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isArray());
    }

    @Test
    @DisplayName("일반 사용자일 경우 본인의 현재 기수 팀 지원 현황만 반환한다")
    public void application_status_should_return_current_batch_applicants_for_member() throws Exception {
        // given
        AuthMember member = new AuthMember(2L, List.of(new SimpleGrantedAuthority("ROLE_MEMBER")));
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities());

        TeamApplicantOverviewDto overviewResponse = new TeamApplicantOverviewDto(
                1L,
                "글로방",
                "홍길동 - ANDROID",
                null,
                "외국인 대상 부동산 매칭 서비스",
                0,
                0,
                0,
                0,
                0
        );

        when(teamMatchService.getCurrentBatchOwnTeam(2L))
                .thenReturn(overviewResponse);

        // when & then
        mockMvc.perform(get("/teams/applications")
                        .with(authentication(authenticationToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.teamId").value(1L));
    }

    @Test
    @DisplayName("팀 지원 목록 조회가 성공적으로 처리되는지 테스트")
    public void get_applications_should_return_team_applicant_response() throws Exception {
        // given
        Long teamId = 1L;
        AuthMember member = new AuthMember(3L, List.of(new SimpleGrantedAuthority("ROLE_MEMBER")));
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities());

        TeamApplicantResponse mockResponse = new TeamApplicantResponse(null, null, null, null, null, null, null);
        when(teamMatchService.getTeamApplicant(3L, teamId, false)).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/teams/{teamId}/applications", teamId)
                        .with(authentication(authenticationToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());
    }

    @TestConfiguration
    static class TestInterceptorConfig {

        @Bean
        public InjectionRoleInterceptor injectionRoleInterceptor() {
            return new InjectionRoleInterceptor(null, null) {
                @Override
                public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                    // 테스트에서 강제로 PM 역할 넣어줌
                    request.setAttribute("role", "PM");
                    return true;
                }
            };
        }
    }
}
