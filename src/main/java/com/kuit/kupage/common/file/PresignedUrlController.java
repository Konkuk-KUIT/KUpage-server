package com.kuit.kupage.common.file;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PresignedUrlController {

    private final PresignedUrlService presignedUrlService;

    @GetMapping("/pre-signed/article/file")
    public PresignedUrlResponse getFilePresignedUrl(@RequestBody ArticlePreSignedFileUrlRequest request) {
        String preSignedUrl = presignedUrlService.getPreSignedUrl("file", request.contentType(), request.contentLength(), request.contentName());
        String fileUrl = preSignedUrl.split("\\?")[0];
        return new PresignedUrlResponse(preSignedUrl, fileUrl);
    }

    @GetMapping("/pre-signed/article/image")
    public PresignedUrlResponse getImagePresignedUrl(@RequestBody ArticlePreSignedImageUrlRequest request) {
        String preSignedUrl = presignedUrlService.getPreSignedUrl("image",  request.contentType(), request.contentLength(), request.contentName());
        String fileUrl = preSignedUrl.split("\\?")[0];
        return new PresignedUrlResponse(preSignedUrl, fileUrl);
    }
}
