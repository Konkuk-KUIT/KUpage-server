package com.kuit.kupage.domain.project.dto;

import lombok.Builder;

@Builder
public record ReviewContent (
        String nameInfo,
        String content
) {
}
