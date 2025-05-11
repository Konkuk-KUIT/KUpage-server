package com.kuit.kupage.common.file;

public record PresignedUrlRequest(
        String contentType,
        Integer contentLength,
        String contentName
) {
}
