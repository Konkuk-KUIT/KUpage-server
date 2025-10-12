package com.kuit.kupage.domain.teamMatch;

import com.kuit.kupage.common.type.BaseEntity;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.teamMatch.dto.TeamMatchRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "team")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamApplicant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_applicant_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 20)
    private String studentId;

    @Embedded
    private Part appliedPart;

    @Column(length = 500)
    private String portfolioUrl;

    @Lob
    private String additionalAnswer1;

    @Lob
    private String additionalAnswer2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public TeamApplicant(TeamMatchRequest request, Member member, Team team) {
        this.name = request.name();
        this.studentId = request.studentId();
        this.appliedPart = request.appliedPart();
        this.portfolioUrl = request.portfolioUrl();
        this.additionalAnswer1 = request.additionalAnswer1();
        this.additionalAnswer2 = request.additionalAnswer2();
        this.member = member;
        this.team = team;
    }
}
