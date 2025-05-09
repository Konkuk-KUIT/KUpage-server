package com.kuit.kupage.domain.article.controller;

import com.kuit.kupage.domain.article.ArticleFacade;
import com.kuit.kupage.domain.article.UploadArticleRequest;
import com.kuit.kupage.domain.article.domain.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleFacade articleFacade;

    @PostMapping("/articles")
    public Long uploadArticle(@RequestBody UploadArticleRequest request) {
        // files, imgs 사이즈와 개수에 대한 validation 필요
        Article article = articleFacade.createArticle(request, 1L);
        return article.getId();
    }
}
