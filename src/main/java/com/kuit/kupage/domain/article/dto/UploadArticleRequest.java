package com.kuit.kupage.domain.article.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UploadArticleRequest(
        @NotBlank
        String title,
        List<String> tags,
        List<UploadBlockRequest> blocks
) {
}
