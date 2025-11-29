package com.kuit.kupage.domain.teamMatch;

import com.kuit.kupage.common.type.BaseEntity;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.teamMatch.dto.TeamMatchRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.kuit.kupage.domain.teamMatch.ApplicantStatus.ROUND1_FAILED;

// - 한 사용자는 2개 이상 지원 불가능 : TeamApplicant에서 동일한 (memberId, 기수, 몇차지원, batch) 는 2개 이하만 가능

@Entity
@Table(name = "team_applicant",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_status_team_applicant_member_team",
                        columnNames = {"status", "member_id", "team_id"}
                ),
                @UniqueConstraint(name = "uk_member_status_slot",
                        columnNames = {"status", "member_id", "slot_no"}
                )
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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
    @Column(length = 20, nullable = false)
    private ApplicantStatus status;

    @Column(name = "slot_no", nullable = false,
            columnDefinition = "TINYINT NOT NULL CHECK (slot_no IN (1,2))"
    )
    private Integer slotNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public TeamApplicant(TeamMatchRequest request, Member member, Team team, ApplicantStatus status, int slotNo) {
        this.appliedPart = request.appliedPart();
        this.motivation = request.motivation();
        this.portfolioUrl = request.portfolioUrl();
        this.status = status;
        this.slotNo = slotNo;
        this.member = member;
        this.team = team;
    }

    public boolean isRejected() {
        return status == ROUND1_FAILED;
    }
}
