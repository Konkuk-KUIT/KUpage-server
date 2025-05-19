package com.kuit.kupage.domain.project.entity;

import com.kuit.kupage.domain.member.Member;
import jakarta.persistence.*;

@Entity
public class MemberProject {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_project_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;
}
