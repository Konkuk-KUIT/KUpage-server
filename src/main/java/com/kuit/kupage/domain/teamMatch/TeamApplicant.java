package com.kuit.kupage.domain.teamMatch;

import com.kuit.kupage.common.type.BaseEntity;
import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.teamMatch.dto.TeamMatchRequest;
import com.kuit.kupage.exception.KupageException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import static com.kuit.kupage.common.response.ResponseCode.ALREADY_COMPLETED_TEAM_MATCH;
import static com.kuit.kupage.domain.teamMatch.ApplicantStatus.*;

// - 한 사용자는 2개 이상 지원 불가능 : TeamApplicant에서 동일한 (memberId, 기수, 몇차지원, batch) 는 2개 이하만 가능

@Entity
@Table(name = "team_applicant",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_status_team_applicant_member_team",
                        columnNames = {"status", "member_id", "team_id"}
                ),
                @UniqueConstraint(name = "uk_member_status_slot",
                        columnNames = {"member_id", "slot_no", "batch"}
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

    @Min(1) @Max(2)
    @Column(nullable = false)
    private Integer slotNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Batch batch;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public TeamApplicant(TeamMatchRequest request, Member member, Team team, ApplicantStatus status, int slotNo, Batch batch) {
        this.appliedPart = request.appliedPart();
        this.motivation = request.motivation();
        this.portfolioUrl = request.portfolioUrl();
        this.status = status;
        this.slotNo = slotNo;
        this.batch = batch;
        this.member = member;
        this.team = team;
    }

    public boolean isRejected() {
        return status == ROUND1_FAILED;
    }

    public void accept() {
        this.status = FINAL_CONFIRMED;
    }

    public void reject() {
        if (this.status == ROUND1_APPLYING) {
            this.status = ROUND1_FAILED;
            return;
        }
        if (this.status == ROUND2_APPLYING) {
            this.status = ROUND2_FAILED;
            return;
        }
        throw new KupageException(ALREADY_COMPLETED_TEAM_MATCH);
    }

}
