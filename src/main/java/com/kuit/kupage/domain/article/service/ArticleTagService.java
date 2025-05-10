package com.kuit.kupage.domain.article.service;

import com.kuit.kupage.domain.article.domain.Article;
import com.kuit.kupage.domain.article.domain.ArticleTag;
import com.kuit.kupage.domain.article.domain.Tag;
import com.kuit.kupage.domain.article.repository.ArticleTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleTagService {

    private final ArticleTagRepository articleTagRepository;

    @Transactional
    public List<ArticleTag> createArticleTags(Article article, List<Tag> tags) {
        List<ArticleTag> articleTags = tags.stream().map(t -> ArticleTag.of(article, t)).toList();
        return articleTagRepository.saveAll(articleTags);
    }

}
