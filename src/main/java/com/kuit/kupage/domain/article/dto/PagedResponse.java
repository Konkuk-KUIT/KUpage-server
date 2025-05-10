package com.kuit.kupage.domain.article.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PagedResponse(
        List<ArticleResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        int remainingPages
) {

}
