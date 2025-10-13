package com.kuit.kupage.domain.teamMatch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuit.kupage.domain.teamMatch.Part;
import com.kuit.kupage.domain.teamMatch.dto.PortfolioUploadResponse;
import com.kuit.kupage.domain.teamMatch.dto.TeamMatchRequest;
import com.kuit.kupage.domain.teamMatch.dto.TeamMatchResponse;
import com.kuit.kupage.domain.teamMatch.service.TeamMatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeamMatchController.class)
@ImportAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
public class TeamMatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeamMatchService teamMatchService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    public void apply() throws Exception {
        TeamMatchResponse mockResponse = new TeamMatchResponse(1L);

        when(teamMatchService.apply(eq(1L), eq(1L), any(TeamMatchRequest.class))).thenReturn(mockResponse);

        TeamMatchRequest request = new TeamMatchRequest(
                "이서연",
                "20204567",
                Part.ANDROID,
                "팀의 안드로이드 앱 개발을 담당하고 싶습니다.",
                "https://portfolio.example.com/seoyeon",
                "사용자 경험을 개선하는 인터페이스 설계에 관심이 있습니다.",
                "이번 프로젝트를 통해 UI/UX와 네이티브 앱 성능 최적화 경험을 쌓고 싶습니다."
        );
        mockMvc.perform(post("/teams/1/match")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.teamApplicantId").value(1));
    }

    @Test
    public void uploadPortfolios() throws Exception {
        String mockUrl = "http://example.com/portfolio.pdf";
        PortfolioUploadResponse mockResponse = new PortfolioUploadResponse(mockUrl);

        when(teamMatchService.uploadPortfolio(any())).thenReturn(mockResponse);

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "portfolio.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Dummy PDF content".getBytes()
        );

        mockMvc.perform(multipart("/portfolios")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.portfolioUrl").value(mockUrl));
    }
}
