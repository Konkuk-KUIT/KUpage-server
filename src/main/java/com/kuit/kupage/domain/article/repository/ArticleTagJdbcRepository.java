package com.kuit.kupage.domain.article.repository;

import com.kuit.kupage.domain.article.domain.ArticleTag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ArticleTagJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void saveAllBatch(List<ArticleTag> articleTags) {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO article_tag(article_id, tag_id) VALUES");

        Map<String, Long> params = new HashMap<>();
        for (int i = 0; i < articleTags.size(); i++) {
            ArticleTag articleTag = articleTags.get(i);

            String articleParamName = "articleId" + i;
            String tagParamName = "tagId" + i;

            params.put(articleParamName, articleTag.getArticle().getId());
            params.put(tagParamName, articleTag.getTag().getId());

            sqlBuilder.append("(:").append(articleParamName)
                    .append(", :").append(tagParamName)
                    .append(")");
            if(i != articleTags.size()-1)
                sqlBuilder.append(",");
        }

        log.info("[ArticleTagJdbcRepository] saveAllBatch sql : " + sqlBuilder.toString());

        jdbcTemplate.execute(sqlBuilder.toString(), params, PreparedStatement::executeUpdate);
    }
}
