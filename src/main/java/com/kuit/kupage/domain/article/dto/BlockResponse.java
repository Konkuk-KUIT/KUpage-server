package com.kuit.kupage.domain.article.dto;

import com.kuit.kupage.domain.article.domain.BlockType;
import lombok.Builder;

public record BlockResponse(Integer position,
                            BlockType type,
                            String properties) {

    public static BlockResponse of(Integer position, BlockType type, String properties) {
        return new BlockResponse(position, type, properties);
    }
}
