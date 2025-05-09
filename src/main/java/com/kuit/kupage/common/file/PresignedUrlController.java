package com.kuit.kupage.common.file;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PresignedUrlController {

    private final PresignedUrlService presignedUrlService;

    @GetMapping("/file/pre-signed")
    public String getFilePresignedUrl(@RequestBody PreSignedUrlRequest request) {
        return presignedUrlService.getPreSignedUrl("file", request.contentName());
    }

    @GetMapping("/image/pre-signed")
    public String getImagePresignedUrl(@RequestBody PreSignedUrlRequest request) {
        return presignedUrlService.getPreSignedUrl("image", request.contentName());
    }
}
