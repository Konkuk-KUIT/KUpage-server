package com.kuit.kupage.domain.article.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.exception.ArticleException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.stream.Stream;

@Entity
@Table(name = "block")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "block_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    private Integer position;
    private BlockType type;

    private String properties; // JSON 직렬화 문자열

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Block of(Article article, Integer position, BlockType type, String properties) {
        Map<String, String> props = Map.of();
        try {
            props = objectMapper.readValue(properties, Map.class);
        } catch (Exception e) {
            throw new ArticleException(ResponseCode.PARSING_ISSUE);
        }

        if(!StringUtils.hasText(props.get("title")))
            throw new ArticleException(ResponseCode.INVALID_TITLE_PROPERTY);

        boolean typeHasUrl = Stream.of(BlockType.IMAGE, BlockType.FILE, BlockType.URL).anyMatch(t -> t == type);
        if(typeHasUrl) {
            if(!StringUtils.hasText(props.get("url")))
                throw new ArticleException(ResponseCode.INVALID_URL_PROPERTY);
        }

        if(type == BlockType.CODE) {
            if(!StringUtils.hasText(props.get("code_lang")))
                throw new ArticleException(ResponseCode.INVALID_CODE_LANG_PROPERTY);
        }

        return new Block(null, article, position, type, properties);
    }
}

