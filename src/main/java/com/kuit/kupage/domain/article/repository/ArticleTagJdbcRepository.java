package com.kuit.kupage.domain.article.repository;

import com.kuit.kupage.domain.article.domain.ArticleTag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ArticleTagJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public void saveAllBatch(List<ArticleTag> articleTags) {
        String sql = "INSERT INTO article_tag(article_id, tag_id) VALUES";

        String values = articleTags.stream().map(articleTag -> "(" +
                articleTag.getArticle().getId() + ", " +
                articleTag.getTag().getId() +
                ")"
        ).collect(Collectors.joining(", "));
        sql += values + ";";

        log.info("[ArticleTagJdbcRepository] saveAllBatch sql : " + sql);

        jdbcTemplate.execute(sql);

    }
}
