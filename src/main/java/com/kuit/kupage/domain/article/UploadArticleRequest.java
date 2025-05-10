package com.kuit.kupage.domain.article;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UploadArticleRequest(
        @NotEmpty
        @Size(max = 50) String title,
        List<String> tags,
        List<UploadBlockRequest> blocks
) {
}
