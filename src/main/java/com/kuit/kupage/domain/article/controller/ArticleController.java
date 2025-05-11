package com.kuit.kupage.domain.article.controller;

import com.kuit.kupage.common.auth.AuthMember;
import com.kuit.kupage.common.response.BaseResponse;
import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.domain.article.domain.Article;
import com.kuit.kupage.domain.article.dto.UploadArticleRequest;
import com.kuit.kupage.domain.article.service.ArticleCreateFacade;
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

    private final ArticleCreateFacade articleCreateFacade;

    @PostMapping("/articles")
    public BaseResponse<UploadArticleResponse> uploadArticle(@Valid @RequestBody UploadArticleRequest request, @AuthenticationPrincipal AuthMember authMember) {
        Article article = articleCreateFacade.createArticle(request, authMember.getId());
        return new BaseResponse<>(ResponseCode.SUCCESS, new UploadArticleResponse(article.getId()));
    }
}
