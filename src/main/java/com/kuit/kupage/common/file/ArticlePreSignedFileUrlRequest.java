package com.kuit.kupage.common.file;

public record ArticlePreSignedFileUrlRequest(
        String contentType,
        Integer contentLength,
        String contentName
) {
}
