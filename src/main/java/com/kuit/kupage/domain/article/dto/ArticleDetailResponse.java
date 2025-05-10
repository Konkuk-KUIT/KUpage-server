package com.kuit.kupage.domain.article.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ArticleDetailResponse(Long id, String authorName, String title, List<BlockResponse> content, LocalDateTime createdAt) {

}
