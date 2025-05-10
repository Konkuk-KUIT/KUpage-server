package com.kuit.kupage.common.file;

import jakarta.validation.constraints.Max;

public record ArticlePreSignedImageUrlRequest(
        String contentType,
        @Max(10*1024*1024)
        String contentLength,
        String contentName
) {
}
