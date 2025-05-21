package com.kuit.kupage.domain.project.entity;

import jakarta.persistence.*;

@Entity
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    private String memberDesc;

    private String review;
}
