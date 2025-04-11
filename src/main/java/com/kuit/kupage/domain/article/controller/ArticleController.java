package com.kuit.kupage.domain.article.controller;

import com.kuit.kupage.domain.article.UploadArticleRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@RestController
@Slf4j
public class ArticleController {

    @PostMapping("/articles")
    public void uploadArticle(
            @RequestPart UploadArticleRequest request,
            @RequestPart MultipartFile[] files,
            @RequestPart MultipartFile[] imgs
    ) {
        log.debug("{}", request.getName());

        log.debug("files.length : {}", files.length);
        log.debug("files names : {}", Arrays.stream(files).map(MultipartFile::getOriginalFilename).toList());
        log.debug("files size : {}", Arrays.stream(files).map(MultipartFile::getSize).toList());

        log.debug("imgs length : {}", imgs.length);
        log.debug("imgs names : {}", Arrays.stream(imgs).map(MultipartFile::getOriginalFilename).toList());
        log.debug("imgs size : {}", Arrays.stream(imgs).map(MultipartFile::getSize).toList());
    }


}
