package com.kuit.kupage.common.file;

import com.kuit.kupage.common.response.BaseResponse;
import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.exception.ArticleException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "PresignedUrl Controller", description = "PresignedUrl 관련 Controller 입니다.")
public class PresignedUrlController {

    private final PresignedUrlService presignedUrlService;
    private final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/png", "image/jpeg", "image/jpg", "image/gif", "image/webp", "image/heic", "image/avif"
    );
    private final static Integer MAX_IMAGE_SIZE = 20 * 1024 * 1024;     // 20MB
    private final static Integer MAX_FILE_SIZE = 50 * 1024 * 1024;      // 50MB

    @PostMapping("/pre-signed/articles/file")
    @Operation(summary = "게시글 파일 업로드 링크 제공 API", description = "로그인 한 유저가 게시글을 위한 파일 업로드 링크를 제공받습니다.")
    public BaseResponse<PresignedUrlResponse> getFilePresignedUrl(@RequestBody PresignedUrlRequest request) {
        if (request.contentLength() > MAX_FILE_SIZE) {
            throw new ArticleException(ResponseCode.TOO_BIG_FILE);
        }

        String preSignedUrl = presignedUrlService.getPreSignedUrl("file", request.contentType(), request.contentLength().toString(), request.contentName());
        String fileUrl = preSignedUrl.split("\\?")[0];
        return new BaseResponse<>(ResponseCode.SUCCESS, new PresignedUrlResponse(preSignedUrl, fileUrl));
    }

    @PostMapping("/pre-signed/articles/image")
    @Operation(summary = "게시글 이미지 업로드 링크 제공 API", description = "로그인 한 유저가 게시글을 위한 이미지 업로드 링크를 제공받습니다.")
    public BaseResponse<PresignedUrlResponse> getImagePresignedUrl(@RequestBody PresignedUrlRequest request) {
        if (ALLOWED_IMAGE_TYPES.stream().noneMatch(t -> t.equals(request.contentType()))) {
            throw new ArticleException(ResponseCode.INVALID_IMAGE_TYPE);
        }
        if (request.contentLength() > MAX_IMAGE_SIZE) {
            throw new ArticleException(ResponseCode.TOO_BIG_IMAGE);
        }

        String preSignedUrl = presignedUrlService.getPreSignedUrl("image", request.contentType(), request.contentLength().toString(), request.contentName());
        String fileUrl = preSignedUrl.split("\\?")[0];
        return new BaseResponse<>(ResponseCode.SUCCESS, new PresignedUrlResponse(preSignedUrl, fileUrl));
    }
}
