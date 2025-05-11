package com.kuit.kupage.domain.article.domain;

import com.kuit.kupage.domain.article.service.BlockPropertyValidator;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
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

    @Enumerated(value = EnumType.STRING)
    private BlockType type;

    private String properties; // JSON 직렬화 문자열

    public static Block of(Article article, Integer position, BlockType type, String properties) {
        BlockPropertyValidator.validateBlockProperties(type, properties);
        return Block.builder()
                .id(null)
                .article(article)
                .position(position)
                .type(type)
                .properties(properties)
                .build();
    }

}

