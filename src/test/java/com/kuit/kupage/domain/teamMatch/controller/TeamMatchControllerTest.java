package com.kuit.kupage.domain.teamMatch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuit.kupage.common.auth.AuthMember;
import com.kuit.kupage.common.config.SecurityTestConfig;
import com.kuit.kupage.domain.teamMatch.Part;
import com.kuit.kupage.domain.teamMatch.dto.PortfolioUploadResponse;
import com.kuit.kupage.domain.teamMatch.dto.TeamMatchRequest;
import com.kuit.kupage.domain.teamMatch.dto.TeamMatchResponse;
import com.kuit.kupage.domain.teamMatch.service.TeamMatchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TeamMatchController.class)
@Import(SecurityTestConfig.class)
public class TeamMatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeamMatchService teamMatchService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("팀 매치 지원 요청이 성공적으로 처리되는지 테스트")
    public void apply() throws Exception {
        // given
        TeamMatchResponse mockResponse = new TeamMatchResponse(1L);
        when(teamMatchService.apply(anyLong(), anyLong(), any(TeamMatchRequest.class))).thenReturn(mockResponse);

        TeamMatchRequest request = new TeamMatchRequest(
                Part.ANDROID,
                "팀의 안드로이드 앱 개발을 담당하고 싶습니다.",
                "https://portfolio.example.com/seoyeon"
        );

        AuthMember authMember = new AuthMember(1L, List.of(new SimpleGrantedAuthority("ROLE_MEMBER")));
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authMember, null, authMember.getAuthorities());

        // when
        mockMvc.perform(post("/teams/1/match")
                        .with(authentication(authenticationToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.teamApplicantId").value(1));
    }

    @Test
    @DisplayName("포트폴리오 파일 업로드가 성공적으로 처리되는지 테스트")
    public void uploadPortfolios() throws Exception {
        // given
        String mockUrl = "https://example.com/portfolio.pdf";
        PortfolioUploadResponse mockResponse = new PortfolioUploadResponse(mockUrl);

        when(teamMatchService.uploadPortfolio(any())).thenReturn(mockResponse);

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "portfolio.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Dummy PDF content".getBytes()
        );

        // when
        mockMvc.perform(multipart("/portfolios")
                        .file(mockFile))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.portfolioUrl").value(mockUrl));
    }
}
