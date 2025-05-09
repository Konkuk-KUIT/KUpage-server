package com.kuit.kupage.domain.article.domain;

import jakarta.persistence.*;

@Entity
public class Tag {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    private String name;
}
