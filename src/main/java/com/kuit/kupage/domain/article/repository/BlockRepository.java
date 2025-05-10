package com.kuit.kupage.domain.article.repository;

import com.kuit.kupage.domain.article.domain.Article;
import com.kuit.kupage.domain.article.domain.Block;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {
    @EntityGraph(attributePaths = {"article"})
    List<Block> findAllByArticle_Id(Long articleId);
}
