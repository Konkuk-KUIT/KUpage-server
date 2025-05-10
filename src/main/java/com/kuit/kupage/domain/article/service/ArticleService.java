package com.kuit.kupage.domain.article.service;

import com.kuit.kupage.domain.article.domain.Article;
import com.kuit.kupage.domain.article.repository.ArticleRepository;
import com.kuit.kupage.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    @Transactional
    public Article createArticle(Member member, String title) {
        Article article = Article.of(member, title);
        return articleRepository.save(article);
    }

}
