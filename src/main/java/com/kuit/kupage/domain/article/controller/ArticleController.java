package com.kuit.kupage.domain.article.controller;

import com.kuit.kupage.common.auth.AuthMember;
import com.kuit.kupage.common.response.BaseResponse;
import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.domain.article.domain.Article;
import com.kuit.kupage.domain.article.dto.UploadArticleRequest;
import com.kuit.kupage.domain.article.service.ArticleCreateFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "게시글 Controller", description = "게시글 관련 Controller 입니다.")
public class ArticleController {

    private final ArticleCreateFacade articleCreateFacade;

    @PostMapping("/articles")
    @Operation(summary = "게시글 등록 API", description = "로그인 한 유저가 게시글 등록을 요청합니다.")
    public BaseResponse<UploadArticleResponse> uploadArticle(
            @Valid @RequestBody UploadArticleRequest request,
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthMember authMember
    ) {
        Article article = articleCreateFacade.createArticle(request, authMember.getId());
        return new BaseResponse<>(ResponseCode.SUCCESS, new UploadArticleResponse(article.getId()));
    }
}
