package com.kuit.kupage.common.file;

public record PresignedUrlResponse(
        String presignedUrl,
        String fileUrl
) {
}
