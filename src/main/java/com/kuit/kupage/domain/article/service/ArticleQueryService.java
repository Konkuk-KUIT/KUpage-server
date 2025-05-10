package com.kuit.kupage.domain.article.service;

import com.kuit.kupage.domain.article.domain.Article;
import com.kuit.kupage.domain.article.domain.ArticleTag;
import com.kuit.kupage.domain.article.dto.ArticleResponse;
import com.kuit.kupage.domain.article.dto.PagedResponse;
import com.kuit.kupage.domain.article.repository.ArticleRepository;
import com.kuit.kupage.domain.article.repository.ArticleTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleQueryService {
    private final ArticleRepository articleRepository;
    private final ArticleTagRepository articleTagRepository;

    private static final int PAGE_SIZE = 16;

    public PagedResponse<ArticleResponse> listArticles(final int page, final String tag) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());

        Page<Article> paged = checkTag(tag, pageable);

        List<ArticleResponse> content = paged.stream()
                .map(ArticleResponse::toArticle)
                .toList();

        return PagedResponse.of(content, paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.getTotalPages() - paged.getNumber() - 1);
    }

    private Page<Article> checkTag(String tag, Pageable pageable) {
        if (tag == null || tag.isBlank())
            return articleRepository.findAll(pageable);
        return articleTagRepository.findByTag_Name(tag, pageable)
                .map(ArticleTag::getArticle);
    }
}
