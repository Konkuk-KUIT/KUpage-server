package com.kuit.kupage.domain.article.domain;

import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.exception.ArticleException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String title;

    private static final Integer MAX_TITLE_LENGTH = 50;
    public static Article of(Member member, String title) {
        if(title.length() > MAX_TITLE_LENGTH)
            throw new ArticleException(ResponseCode.INVALID_TITLE);
        return new Article(null, member, title);
    }
}
