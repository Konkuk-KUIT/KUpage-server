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

        // NOTE: Presigned PUT은 서명에 포함된 헤더/파라미터가 클라이언트 요청과 100% 일치해야 합니다.
        // - 브라우저 환경에서는 Content-Length를 직접 지정하기 어렵고,
        // - ACL(PUBLIC_READ)을 넣으면 x-amz-acl 헤더가 SignedHeaders에 포함되어 누락 시 403이 발생할 수 있습니다.
        // 따라서 presign에는 content-length/acl을 포함하지 않고, 공개 접근은 버킷 정책/CloudFront 등으로 제어하는 것을 권장합니다.
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

        // content-type은 presign에 포함될 수 있으므로, 프론트 PUT에서도 동일하게 보내도록 유지합니다.
        if (StringUtils.hasText(contentType)) {
            builder.contentType(contentType);
        }

        // IMPORTANT:
        // - contentLength/acl은 presign 서명 조건을 까다롭게 만들어(특히 브라우저에서) 403을 유발하기 쉬워 제외합니다.
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
