package com.kuit.kupage.domain.article.dto;

import com.kuit.kupage.domain.article.domain.Article;

import java.time.LocalDateTime;

public record ArticleResponse(Long id, Long memberId, String authorName, String title, LocalDateTime createdAt) {
    public static ArticleResponse from(Article article) {
        return new ArticleResponse(article.getId(), article.getMember().getId(), article.getMember().getName(), article.getTitle(), article.getCreatedAt());
    }
}
