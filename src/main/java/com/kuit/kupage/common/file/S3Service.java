package com.kuit.kupage.common.file;

import com.kuit.kupage.exception.KupageException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

import static com.kuit.kupage.common.response.ResponseCode.AWS_S3_UPLOAD_ISSUE;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;
    @Value("${cloud.aws.cloudfront.deploy-url}")
    private String cloudFrontUrl;

    public String uploadImage(MultipartFile file) {
        String extension = Objects.requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        String s3FileName = "image/" + UUID.randomUUID().toString().substring(0, 10) + "." + extension;
        return uploadToS3(file, s3FileName);
    }

    public String uploadFile(MultipartFile file) {
        String extension = Objects.requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        String s3FileName = "file/" + UUID.randomUUID().toString().substring(0, 10) + "." + extension;
        return uploadToS3(file, s3FileName);
    }

    private String uploadToS3(MultipartFile file, String s3FileName) {
        try (InputStream in = file.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3FileName)
                    .contentType(file.getContentType())
                    // Public Access Block 비활성화 전제: 업로드 객체를 public-read로 설정
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(in, file.getSize())
            );
        } catch (Exception e) {
            throw new KupageException(AWS_S3_UPLOAD_ISSUE);
        }

        return cloudFrontUrl + "/" + s3FileName;
    }
}
