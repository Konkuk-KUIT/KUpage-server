package com.kuit.kupage.unit.common;

import com.kuit.kupage.common.file.S3Service;
import com.kuit.kupage.exception.KupageException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class S3ServiceTest {

    @Test
    @DisplayName("파일 업로드 성공")
    void test() {
        // given
        S3Client s3Client = mock(S3Client.class);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().eTag("etag").build());

        S3Service s3Service = new S3Service(s3Client);

        // 테스트에서는 반환 URL이 '/image/..', '/file/..' 형태가 되도록 cloudFrontUrl을 빈 문자열로 둡니다.
        // (실서비스에서는 cloudFrontUrl이 'https://xxxx.cloudfront.net' 처럼 들어갈 수 있음)
        ReflectionTestUtils.setField(s3Service, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(s3Service, "cloudFrontUrl", "");

        TestMultipartFile image = new TestMultipartFile("testImage.png", "application/png");
        TestMultipartFile file = new TestMultipartFile("testFile.pdf", "application/pdf");

        // when
        String url1 = s3Service.uploadImage(image);
        String url2 = s3Service.uploadFile(file);

        // then
        String[] url1Tokens = url1.split("/");
        assertThat(url1Tokens[1]).isEqualTo("image");
        assertThat(url1Tokens[2].endsWith(".png")).isTrue();

        String[] url2Tokens = url2.split("/");
        assertThat(url2Tokens[1]).isEqualTo("file");
        assertThat(url2Tokens[2].endsWith(".pdf")).isTrue();
    }

    @Test
    @DisplayName("파일 업로드 실패")
    void testFail() {
        // given
        S3Client s3Client = mock(S3Client.class);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(new RuntimeException("업로드 실패"));

        S3Service s3Service = new S3Service(s3Client);
        ReflectionTestUtils.setField(s3Service, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(s3Service, "cloudFrontUrl", "");

        TestMultipartFile image = new TestMultipartFile("testFailImage.png", "application/png");
        TestMultipartFile file = new TestMultipartFile("testFailFile.pdf", "application/pdf");

        // then
        assertThrows(KupageException.class, () -> s3Service.uploadImage(image));
        assertThrows(KupageException.class, () -> s3Service.uploadFile(file));
    }

    static class TestMultipartFile implements MultipartFile {

        public TestMultipartFile(String data, String type) {
            this.data = data;
            this.type = type;
        }

        private final String data;
        private final String type;

        @Override
        public String getName() {
            return data;
        }

        @Override
        public String getOriginalFilename() {
            return data;
        }

        @Override
        public String getContentType() {
            return type;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public long getSize() {
            return data.length();
        }

        @Override
        public byte[] getBytes() {
            return data.getBytes();
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(data.getBytes());
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            // no-op for test
        }
    }
}
