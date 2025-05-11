package com.kuit.kupage.domain.article.repository;

import com.kuit.kupage.domain.article.domain.Block;
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
public class BlockJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void saveAllBatch(List<Block> blocks) {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO block(article_id, position, type, properties) VALUES");

        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);

            String articleParamName = "articleId" + i;
            String positionParmName = "position" + i;
            String typeParamName = "type" + i;
            String propertiesParamName = "properties" + i;

            params.put(articleParamName, block.getArticle().getId().toString());
            params.put(positionParmName, block.getPosition().toString());
            params.put(typeParamName, block.getType().toString());
            params.put(propertiesParamName, block.getProperties());

            sqlBuilder.append("(:").append(articleParamName)
                    .append(", :").append(positionParmName)
                    .append(", :").append(typeParamName)
                    .append(", :").append(propertiesParamName)
                    .append(")");
            if(i != blocks.size()-1)
                sqlBuilder.append(",");
        }

        log.info("[BlockJdbcRepository] saveAllBatch sql : " + sqlBuilder.toString());

        jdbcTemplate.execute(sqlBuilder.toString(), params, PreparedStatement::executeUpdate);
    }
}
