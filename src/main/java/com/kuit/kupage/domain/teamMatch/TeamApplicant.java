package com.kuit.kupage.domain.teamMatch;

import com.kuit.kupage.common.type.BaseEntity;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.teamMatch.dto.TeamMatchRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "team_applicant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamApplicant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_applicant_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Part appliedPart;

    @Column(length = 1000)
    private String motivation;

    @Column(length = 500)
    private String portfolioUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public TeamApplicant(TeamMatchRequest request, Member member, Team team) {
        this.appliedPart = request.appliedPart();
        this.motivation = request.motivation();
        this.portfolioUrl = request.portfolioUrl();
        this.member = member;
        this.team = team;
    }
}
