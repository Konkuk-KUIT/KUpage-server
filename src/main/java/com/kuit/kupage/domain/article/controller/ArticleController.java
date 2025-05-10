package com.kuit.kupage.domain.article.controller;

import com.kuit.kupage.common.auth.AuthMember;
import com.kuit.kupage.common.response.BaseResponse;
import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.domain.article.domain.Article;
import com.kuit.kupage.domain.article.dto.UploadArticleRequest;
import com.kuit.kupage.domain.article.service.ArticleFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleFacade articleFacade;

    @PostMapping("/articles")
    public BaseResponse<UploadArticleResponse> uploadArticle(@Valid @RequestBody UploadArticleRequest request, @AuthenticationPrincipal AuthMember authMember) {
        Article article = articleFacade.createArticle(request, 1L);
        return new BaseResponse<>(ResponseCode.SUCCESS, new UploadArticleResponse(article.getId()));
    }
}
