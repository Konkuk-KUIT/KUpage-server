package com.kuit.kupage.common;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.exception.KupageException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static com.kuit.kupage.common.response.ResponseCode.*;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client s3Client;
    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;
    @Value("${cloud.aws.cloudfront.deploy-url}")
    private String cloudFrontUrl;

    public String uploadImage(MultipartFile file) {
        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
        String s3FileName = "image/" + UUID.randomUUID().toString().substring(0, 10) + "." + extension;
        return uploadToS3(file, s3FileName);
    }

    public String uploadFile(MultipartFile file) {
        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
        String s3FileName = "file/" + UUID.randomUUID().toString().substring(0, 10) + "." + extension;
        return uploadToS3(file, s3FileName);
    }

    private String uploadToS3(MultipartFile file, String s3FileName) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        try {
            PutObjectRequest s3Object = new PutObjectRequest(bucketName, s3FileName, file.getInputStream(), objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            s3Client.putObject(s3Object);
        } catch (Exception e) {
            throw new KupageException(AWS_S3_UPLOAD_ISSUE);
        }

        return cloudFrontUrl + "/" + s3FileName;
    }
}
