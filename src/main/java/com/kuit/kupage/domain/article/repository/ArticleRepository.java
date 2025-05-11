package com.kuit.kupage.domain.article.repository;

import com.kuit.kupage.domain.article.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
