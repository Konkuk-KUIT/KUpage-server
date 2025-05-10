package com.kuit.kupage.common.file;

public record ArticlePreSignedImageUrlRequest(
        String contentType,
        Integer contentLength,
        String contentName
) {
}
