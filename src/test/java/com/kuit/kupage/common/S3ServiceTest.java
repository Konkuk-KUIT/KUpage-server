package com.kuit.kupage.common;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.kuit.kupage.common.file.S3Service;
import com.kuit.kupage.exception.KupageException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class S3ServiceTest {

    @Test
    @DisplayName("파일 업로드 성공")
    void test() {
        // given
        S3Service s3Service = new S3Service(new TestAmazonClient());
        TestMultipartFile image = new TestMultipartFile("testImage.png", "application/png");
        TestMultipartFile file = new TestMultipartFile("testFile.pdf", "application/pdf");

        // when
        String url1 = s3Service.uploadImage(image);
        String url2 = s3Service.uploadFile(file);

        // then
        System.out.println("url1 = " + url1);
        String[] url1Tokens = url1.split("/");
        assertThat(url1Tokens[1]).isEqualTo("image");
        assertThat(url1Tokens[2].endsWith(".png")).isEqualTo(true);

        System.out.println("url1 = " + url1);
        String[] url2Tokens = url2.split("/");
        assertThat(url2Tokens[1]).isEqualTo("file");
        assertThat(url2Tokens[2].endsWith(".pdf")).isEqualTo(true);
    }

    @Test
    @DisplayName("파일 업로드 실패")
    void testFail() {
        // given
        S3Service s3Service = new S3Service(new TestAmazonClient());
        TestMultipartFile image = new TestMultipartFile("testFailImage.png", "application/png");
        TestMultipartFile file = new TestMultipartFile("testFailFile.pdf", "application/pdf");

        // when

        // then
        assertThrows(KupageException.class, () -> s3Service.uploadImage(image));
        assertThrows(KupageException.class, () -> s3Service.uploadFile(file));
    }

    static class TestAmazonClient extends AmazonS3Client {
        @Override
        public PutObjectResult putObject(PutObjectRequest putObjectRequest) throws SdkClientException, AmazonServiceException {
            String key = putObjectRequest.getKey();
            long contentLength = putObjectRequest.getMetadata().getContentLength();
            String contentType = putObjectRequest.getMetadata().getContentType();
            String content = "";
            try {
                byte[] bytes = putObjectRequest.getInputStream().readAllBytes();
                content = new String(bytes, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println("===== 업로드 파일 정보 =====");
            System.out.println("key = " + key);
            System.out.println("content = " + content);
            System.out.println("contentLength = " + contentLength);
            System.out.println("contentType = " + contentType);
            System.out.println();

            if (content.contains("Fail"))
                throw new AmazonServiceException("업로드 실패");

            return new PutObjectResult();
        }
    }

    static class TestMultipartFile implements MultipartFile {

        public TestMultipartFile(String data, String type) {
            this.data = data;
            this.type = type;
        }

        private String data;
        private String type;

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
        public byte[] getBytes() throws IOException {
            return data.getBytes();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data.getBytes());
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {

        }
    }
}
