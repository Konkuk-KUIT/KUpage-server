package com.kuit.kupage.domain.article;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.domain.article.domain.BlockType;
import com.kuit.kupage.exception.ArticleException;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.stream.Stream;

public class BlockPropertyValidator {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int SUB_TITLE_MAX_TITLE_LENGTH = 50;

    public static void validateBlockProperties(BlockType type, String properties) {
        Map<String, String> props = Map.of();
        try {
            props = objectMapper.readValue(properties, Map.class);
        } catch (Exception e) {
            throw new ArticleException(ResponseCode.PARSING_ISSUE);
        }

        validateTitleProperty(type, props.get("title"));
        validateUrlProperty(type, props.get("url"));
        validateCodLangProperty(type, props.get("code_lang"));
    }

    private static void validateCodLangProperty(BlockType type, String codeLang) {
        if(type == BlockType.CODE) {
            if(!StringUtils.hasText(codeLang))
                throw new ArticleException(ResponseCode.INVALID_CODE_LANG_PROPERTY);
        }
    }

    private static void validateUrlProperty(BlockType type, String url) {
        boolean typeHasUrl = Stream.of(BlockType.IMAGE, BlockType.FILE, BlockType.URL).anyMatch(t -> t == type);
        if(typeHasUrl) {
            if(!StringUtils.hasText(url))
                throw new ArticleException(ResponseCode.INVALID_URL_PROPERTY);
        }
    }

    private static void validateTitleProperty(BlockType type, String title) {
        if(!StringUtils.hasText(title))
            throw new ArticleException(ResponseCode.INVALID_TITLE_PROPERTY);
        if(type == BlockType.SUB_TITLE && title.length() >= SUB_TITLE_MAX_TITLE_LENGTH)
            throw new ArticleException(ResponseCode.INVALID_TITLE_PROPERTY);
    }

}
