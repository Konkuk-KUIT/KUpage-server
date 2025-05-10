package com.kuit.kupage.domain.article.repository;

import com.kuit.kupage.domain.article.domain.Block;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class BlockJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public void saveAllBatch(List<Block> blocks) {
        String sql = "INSERT INTO block(article_id, position, type, properties) VALUES";

        String values = blocks.stream().map(block -> "(" +
                block.getArticle().getId() + ", " +
                block.getPosition() + ", " +
                '\'' + block.getType() + '\'' + ", " +
                '\'' + block.getProperties() + '\'' +
                ")"
        ).collect(Collectors.joining(", "));
        sql += values + ";";

        log.info("[BlockJdbcRepository] saveAllBatch sql : " + sql);

        jdbcTemplate.execute(sql);
    }
}
