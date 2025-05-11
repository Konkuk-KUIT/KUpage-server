package com.kuit.kupage.common.file;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PresignedUrlService {
    @Value("${cloud.aws.s3.bucket-name}")
    private String bucket;
    private final AmazonS3 amazonS3;

    /**
     * presigned url 발급
     *
     * @param prefix   버킷 디렉토리 이름
     * @param fileName 클라이언트가 전달한 파일명 파라미터
     * @return presigned url
     */
    public String getPreSignedUrl(String prefix, String contentType, String contentLength, String fileName) {
        if (StringUtils.hasText(prefix)) {
            fileName = createPath(prefix, fileName);
        }

        GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePreSignedUrlRequest(bucket, contentType, contentLength, fileName);
        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }

    /**
     * 파일 업로드용(PUT) presigned url 생성
     *
     * @param bucket   버킷 이름
     * @param fileName S3 업로드용 파일 이름
     * @return presigned url
     */
    private GeneratePresignedUrlRequest getGeneratePreSignedUrlRequest(String bucket, String contentType, String contentLength, String fileName) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, fileName)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(getPreSignedUrlExpiration());
        generatePresignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL,
                CannedAccessControlList.PublicRead.toString());
        generatePresignedUrlRequest.putCustomRequestHeader(HTTP.CONTENT_TYPE, contentType);
        generatePresignedUrlRequest.putCustomRequestHeader(HTTP.CONTENT_LEN, contentLength);
        return generatePresignedUrlRequest;
    }

    /**
     * presigned url 유효 기간 설정
     *
     * @return 유효기간
     */
    private Date getPreSignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 2;
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    /**
     * 파일의 전체 경로를 생성
     *
     * @param prefix 디렉토리 경로
     * @return 파일의 전체 경로
     */
    private String createPath(String prefix, String fileName) {
        String fileId = createFileId();
        return String.format("%s/%s", prefix, fileId + fileName);
    }

    /**
     * 파일 고유 ID를 생성
     *
     * @return 36자리의 UUID
     */
    private String createFileId() {
        return UUID.randomUUID().toString();
    }
}
