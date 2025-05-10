package com.kuit.kupage.domain.article.controller;

import com.kuit.kupage.domain.article.dto.ArticleResponse;
import com.kuit.kupage.domain.article.dto.PagedResponse;
import com.kuit.kupage.domain.article.service.ArticleQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticleQueryController.class)
class ArticleQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArticleQueryService articleQueryService;

    @Test
    @DisplayName("페이지당 16개, 태그 필터 조회 성공")
    @WithMockUser
    void listByTag_ReturnsPagedArticles() throws Exception {
        // given
        int page = 0;
        String tag = "backend";
        ArticleResponse a1 = new ArticleResponse(1L, 1L, "Author1", "Title1", LocalDateTime.of(2025, 5, 10, 14, 0));
        ArticleResponse a2 = new ArticleResponse(2L, 2L, "Author2", "Title2", LocalDateTime.of(2025, 5, 10, 13, 0));
        PagedResponse<ArticleResponse> paged = PagedResponse.of(
                List.of(a1, a2),
                page,
                16,
                2,
                1,
                0
        );
        given(articleQueryService.listArticles(page, tag)).willReturn(paged);

        // when & then
        mockMvc.perform(
                        get("/articles")
                                .param("page", String.valueOf(page))
                                .param("tag", tag)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.page").value(page))
                .andExpect(jsonPath("$.result.size").value(16))
                .andExpect(jsonPath("$.result.totalElements").value(2))
                .andExpect(jsonPath("$.result.totalPages").value(1))
                .andExpect(jsonPath("$.result.remainingPages").value(0))
                .andExpect(jsonPath("$.result.content", hasSize(2)))
                .andExpect(jsonPath("$.result.content[0].id").value(1))
                .andExpect(jsonPath("$.result.content[0].title").value("Title1"));
    }


}