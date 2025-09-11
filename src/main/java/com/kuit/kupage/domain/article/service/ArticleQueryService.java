package com.kuit.kupage.domain.article.service;

import com.kuit.kupage.common.response.PagedResponse;
import com.kuit.kupage.domain.article.domain.Article;
import com.kuit.kupage.domain.article.domain.ArticleTag;
import com.kuit.kupage.domain.article.dto.ArticleDetailResponse;
import com.kuit.kupage.domain.article.dto.ArticleResponse;
import com.kuit.kupage.domain.article.dto.BlockResponse;
import com.kuit.kupage.domain.article.repository.ArticleRepository;
import com.kuit.kupage.domain.article.repository.ArticleTagRepository;
import com.kuit.kupage.domain.article.repository.BlockRepository;
import com.kuit.kupage.exception.ArticleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.kuit.kupage.common.response.ResponseCode.NOT_FOUND_ARTICLE;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleQueryService {
    private final ArticleRepository articleRepository;
    private final ArticleTagRepository articleTagRepository;
    private final BlockRepository blockRepository;

    private static final int PAGE_SIZE = 16;

    public PagedResponse<ArticleResponse> listArticles(final int page, final String tag) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());

        Page<Article> paged = findArticlesByTagIfPresent(tag, pageable);

        List<ArticleResponse> content = paged.stream()
                .map(ArticleResponse::from)
                .toList();

        return PagedResponse.of(content, paged);
    }

    public ArticleDetailResponse detailById(final Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleException(NOT_FOUND_ARTICLE));

        List<BlockResponse> blockResponses = blockRepository
                .findAllByArticle_Id(articleId)
                .stream()
                .map(block ->
                        BlockResponse.of(block.getPosition(), block.getType(), block.getProperties())
                )
                .toList();

        return ArticleDetailResponse.builder()
                .id(articleId)
                .authorName(article.getMember().getName())
                .title(article.getTitle())
                .content(blockResponses)
                .build();
    }

    private Page<Article> findArticlesByTagIfPresent(String tag, Pageable pageable) {
        if (!StringUtils.hasText(tag))
            return articleRepository.findAll(pageable);
        return articleTagRepository.findByTag_Name(tag, pageable)
                .map(ArticleTag::getArticle);
    }
}
