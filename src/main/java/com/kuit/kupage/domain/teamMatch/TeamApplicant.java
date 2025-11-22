package com.kuit.kupage.domain.teamMatch;

import com.kuit.kupage.common.type.BaseEntity;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.teamMatch.dto.TeamMatchRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.kuit.kupage.domain.teamMatch.ApplicantStatus.ROUND1_FAILED;

// - 한 사용자는 2개 이상 지원 불가능 : TeamApplicant에서 동일한 (memberId, 기수, 몇차지원, batch) 는 2개 이하만 가능

@Entity
@Table(name = "team_applicant",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_team_applicant_member_team",
                        columnNames = {"status", "member_id", "team_id"}
                )
        })
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

    @Enumerated(value = EnumType.STRING)
    private ApplicantStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public TeamApplicant(TeamMatchRequest request, Member member, Team team, ApplicantStatus status) {
        this.appliedPart = request.appliedPart();
        this.motivation = request.motivation();
        this.portfolioUrl = request.portfolioUrl();
        this.status = status;
        this.member = member;
        this.team = team;
    }

    public boolean isRejected() {
        return this.getStatus() == ROUND1_FAILED;
    }
}
