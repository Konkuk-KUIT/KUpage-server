package com.kuit.kupage.common.file;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class PresignedUrlService {

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucket;
    private final S3Presigner s3Presigner;

    public String getPreSignedUrl(String prefix, String contentType, String contentLength, String fileName) {
        if (StringUtils.hasText(prefix)) {
            fileName = createPath(prefix, fileName);
        }

        PutObjectRequest putObjectRequest = buildPutObjectRequest(bucket, fileName, contentType);

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(2)) // 유효기간: 2분
                .putObjectRequest(putObjectRequest)
                .build();

        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }

    private PutObjectRequest buildPutObjectRequest(String bucket, String key, String contentType) {
        PutObjectRequest.Builder builder = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key);

        if (StringUtils.hasText(contentType)) {
            builder.contentType(contentType);
        }
        return builder.build();
    }

    /**
     * 파일의 전체 경로를 생성
     */
    private String createPath(String prefix, String fileName) {
        String fileId = createFileId();
        return String.format("%s/%s", prefix, fileId + fileName);
    }

    /**
     * 파일 고유 ID를 생성
     */
    private String createFileId() {
        return UUID.randomUUID().toString();
    }
}
