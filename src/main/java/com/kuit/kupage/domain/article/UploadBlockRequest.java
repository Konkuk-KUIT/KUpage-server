package com.kuit.kupage.domain.article;

import com.kuit.kupage.domain.article.domain.BlockType;

public record UploadBlockRequest(
        BlockType type,
        Integer position,
        String properties
) {
}
