package com.kuit.kupage.domain.project.entity;

import jakarta.persistence.*;

@Entity
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne
    private Project project;

    private String memberDesc;

    private String review;
}
