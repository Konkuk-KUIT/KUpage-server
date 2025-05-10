package com.kuit.kupage.common.file;

import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.exception.ArticleException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PresignedUrlController {

    private final PresignedUrlService presignedUrlService;
    private final List<String> ALLOWED_IMAGE_TYPES = List.of("image/png", "image/jpeg", "image/gif");
    private final Integer MAX_IMAGE_SIZE = 10*1024*1024; //10MB
    private final Integer MAX_FILE_SIZE = 20*1024*1024; //20MB

    @GetMapping("/pre-signed/article/file")
    public PresignedUrlResponse getFilePresignedUrl(@RequestBody ArticlePreSignedFileUrlRequest request) {
        if(request.contentLength() > MAX_FILE_SIZE) {
            throw new ArticleException(ResponseCode.TOO_BIG_FILE);
        }

        String preSignedUrl = presignedUrlService.getPreSignedUrl("file", request.contentType(), request.contentLength().toString(), request.contentName());
        String fileUrl = preSignedUrl.split("\\?")[0];
        return new PresignedUrlResponse(preSignedUrl, fileUrl);
    }

    @GetMapping("/pre-signed/article/image")
    public PresignedUrlResponse getImagePresignedUrl(@RequestBody ArticlePreSignedImageUrlRequest request) {
        if(ALLOWED_IMAGE_TYPES.stream().noneMatch(t-> t.equals(request.contentType()))) {
            throw new ArticleException(ResponseCode.INVALID_IMAGE_TYPE);
        }
        if(request.contentLength() > MAX_IMAGE_SIZE) {
            throw new ArticleException(ResponseCode.TOO_BIG_IMAGE);
        }

        String preSignedUrl = presignedUrlService.getPreSignedUrl("image",  request.contentType(), request.contentLength().toString(), request.contentName());
        String fileUrl = preSignedUrl.split("\\?")[0];
        return new PresignedUrlResponse(preSignedUrl, fileUrl);
    }
}
