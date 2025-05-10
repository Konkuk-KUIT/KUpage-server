package com.kuit.kupage.common.file;

import jakarta.validation.constraints.Max;

public record ArticlePreSignedFileUrlRequest(
        String contentType,
        @Max(20*1024*1024)
        String contentLength,
        String contentName
) {
}
