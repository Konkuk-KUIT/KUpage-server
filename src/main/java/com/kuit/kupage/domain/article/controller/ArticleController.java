package com.kuit.kupage.domain.article.controller;

import com.kuit.kupage.common.S3Service;
import com.kuit.kupage.domain.article.UploadArticleRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ArticleController {

    private final S3Service s3Service;

    @PostMapping("/articles")
    public void uploadArticle(
            @RequestPart UploadArticleRequest request,
            @RequestPart MultipartFile[] files,
            @RequestPart MultipartFile[] imgs
    ) {
        // files, imgs 사이즈와 개수에 대한 validation 필요
        // servlet 선에서 각 이미지 or 파일이 20MB을 초과하거나, 모든 이미지 + 파일이 160MB(10MB x 10 + 20MB x 3)초과이면 컷
        // 여기선 좀 더 상세하게 처리할 필요 있음
        log.debug("{}", request.getName());

        log.debug("files.length : {}", files.length);
        log.debug("files names : {}", Arrays.stream(files).map(MultipartFile::getOriginalFilename).toList());
        log.debug("files size : {}", Arrays.stream(files).map(MultipartFile::getSize).toList());

        log.debug("imgs length : {}", imgs.length);
        log.debug("imgs names : {}", Arrays.stream(imgs).map(MultipartFile::getOriginalFilename).toList());
        log.debug("imgs size : {}", Arrays.stream(imgs).map(MultipartFile::getSize).toList());

        String imageUrl = s3Service.uploadImage(imgs[0]);
        String fileUrl = s3Service.uploadFile(files[0]);
        log.debug("imgs[0] url : {}", imageUrl);
        log.debug("files[0] url : {}", fileUrl);
    }
}
