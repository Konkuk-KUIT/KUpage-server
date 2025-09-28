package com.kuit.kupage.common.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        int remainingPages
) {
    public static <T, E> PagedResponse<T> of(List<T> content, Page<E> page) {
        return new PagedResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getTotalPages() - page.getNumber() - 1
        );
    }

    public static <T> PagedResponse<T> of(List<T> content, int number, int size, int totalElements, int totalPages) {
        return new PagedResponse<>(
                content,
                number,
                size,
                totalElements,
                totalPages,
                totalPages - number - 1
        );
    }
}
