package com.kuit.kupage.domain.article.controller;

import com.kuit.kupage.common.response.BaseResponse;
import com.kuit.kupage.domain.article.dto.ArticleDetailResponse;
import com.kuit.kupage.domain.article.dto.ArticleResponse;
import com.kuit.kupage.domain.article.dto.PagedResponse;
import com.kuit.kupage.domain.article.service.ArticleQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.kuit.kupage.common.response.ResponseCode.SUCCESS;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/articles")
public class ArticleQueryController {
    private final ArticleQueryService articleQueryService;

    @GetMapping
    public BaseResponse<PagedResponse> list(@RequestParam(defaultValue = "0") final int page,
                                                             @RequestParam(required = false) final String tag) {
        return new BaseResponse<>(articleQueryService.listArticles(page, tag));
    }

    @GetMapping("/{articleId}")
    public BaseResponse<ArticleDetailResponse> detail(@PathVariable final Long articleId) {
        return new BaseResponse<>(articleQueryService.detailById(articleId));
    }
}
