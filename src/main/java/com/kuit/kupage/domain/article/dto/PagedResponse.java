package com.kuit.kupage.domain.article.dto;

import java.util.List;

public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        int remainingPages
) {
    public static <T> PagedResponse<T> of(List<T> content, int page, int size, long totalElements, int totalPages, int remainingPages) {
        return new PagedResponse<>(content, page, size, totalElements, totalPages, remainingPages);
    }
}
