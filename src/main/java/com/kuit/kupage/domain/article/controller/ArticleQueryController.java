package com.kuit.kupage.domain.article.controller;

import com.kuit.kupage.common.response.BaseResponse;
import com.kuit.kupage.domain.article.dto.ArticleResponse;
import com.kuit.kupage.domain.article.dto.PagedResponse;
import com.kuit.kupage.domain.article.service.ArticleQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.kuit.kupage.common.response.ResponseCode.SUCCESS;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/articles")
public class ArticleQueryController {
    private final ArticleQueryService articleQueryService;

    @GetMapping
    public BaseResponse<PagedResponse<ArticleResponse>> list(@RequestParam(defaultValue = "0") final int page,
                                                             @RequestParam(required = false) final String tag) {
        return new BaseResponse<>(SUCCESS, articleQueryService.listArticles(page, tag));
    }
}
