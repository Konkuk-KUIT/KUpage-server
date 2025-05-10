package com.kuit.kupage.domain.article.repository;

import com.kuit.kupage.domain.article.domain.ArticleTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleTagRepository extends JpaRepository<ArticleTag, Long> {
    @EntityGraph(attributePaths = {"tag", "article"})
    Page<ArticleTag> findByTag_Name(String tagName, Pageable pageable);
}
