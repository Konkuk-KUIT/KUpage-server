package com.kuit.kupage.domain.article;

import com.kuit.kupage.domain.article.domain.BlockType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;

public record UploadBlockRequest(
        BlockType type,
        @PositiveOrZero
        Integer position,
        @NotEmpty
        String properties
) {
}
