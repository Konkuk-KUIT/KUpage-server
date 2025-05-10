package com.kuit.kupage.common.file;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PresignedUrlController {

    private final PresignedUrlService presignedUrlService;

    @GetMapping("/article/file/pre-signed")
    public PresignedUrlResponse getFilePresignedUrl(@RequestBody ArticlePreSignedFileUrlRequest request) {
        String preSignedUrl = presignedUrlService.getPreSignedUrl("file", request.contentType(), request.contentLength(), request.contentName());
        String fileUrl = preSignedUrl.split("\\?")[0];
        return new PresignedUrlResponse(preSignedUrl, fileUrl);
    }

    @GetMapping("/article/image/pre-signed")
    public PresignedUrlResponse getImagePresignedUrl(@RequestBody ArticlePreSignedImageUrlRequest request) {
        String preSignedUrl = presignedUrlService.getPreSignedUrl("image",  request.contentType(), request.contentLength(), request.contentName());
        String fileUrl = preSignedUrl.split("\\?")[0];
        return new PresignedUrlResponse(preSignedUrl, fileUrl);
    }
}
