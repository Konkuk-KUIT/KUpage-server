package com.kuit.kupage.domain.article.repository;

import com.kuit.kupage.domain.article.domain.Article;
import com.kuit.kupage.domain.article.domain.ArticleTag;
import com.kuit.kupage.domain.article.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleTagRepository extends JpaRepository<ArticleTag, Long> {
    List<ArticleTag> findArticleTagsByArticleAndTagIn(Article article, List<Tag> tags);
}
