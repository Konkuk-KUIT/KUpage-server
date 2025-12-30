package com.kuit.kupage.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

    /**
     * awspring 3.x 설정 키를 사용합니다.
     * - application.yml: spring.cloud.aws.region.static: ap-northeast-2
     */
    @Value("${spring.cloud.aws.region.static:ap-northeast-2}")
    private String region;

    /**
     * S3Client (AWS SDK v2)
     * - credentials는 지정하지 않으면 DefaultCredentialsProviderChain(EC2 Role/환경변수/로컬 설정 등)을 사용합니다.
     * - awspring starter가 이미 S3Client 빈을 만들 수도 있으므로, 중복 생성을 피하기 위해 @ConditionalOnMissingBean을 사용합니다.
     */
    @Bean
    @ConditionalOnMissingBean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .build();
    }

    /**
     * Presigned URL 생성에 사용하는 Presigner (AWS SDK v2)
     * - 필요 시 서비스에서 주입 받아 presignPutObject/presignGetObject 등에 사용합니다.
     */
    @Bean
    @ConditionalOnMissingBean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(region))
                .build();
    }
}
