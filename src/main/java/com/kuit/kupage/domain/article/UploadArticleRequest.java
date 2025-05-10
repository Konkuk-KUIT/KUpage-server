package com.kuit.kupage.domain.article;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UploadArticleRequest(
        @NotEmpty
        String title,
        List<String> tags,
        List<UploadBlockRequest> blocks
) {
}
