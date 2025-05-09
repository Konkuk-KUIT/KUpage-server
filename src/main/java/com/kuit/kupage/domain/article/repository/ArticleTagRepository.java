package com.kuit.kupage.domain.article.repository;

import com.kuit.kupage.domain.article.domain.ArticleTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleTagRepository extends JpaRepository<ArticleTag, Long> {
}
