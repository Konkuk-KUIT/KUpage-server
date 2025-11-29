package com.kuit.kupage.domain.teamMatch.repository;

import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.teamMatch.ApplicantStatus;
import com.kuit.kupage.domain.teamMatch.Team;
import com.kuit.kupage.domain.teamMatch.TeamApplicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamApplicantRepository extends JpaRepository<TeamApplicant, Long> {
    @Query("select ta.slotNo from TeamApplicant ta where ta.member = :member and ta.status = :status")
    List<Integer> findSlotNosByMemberAndStatus(Member member, ApplicantStatus status);

    long countByMemberAndStatus(Member member, ApplicantStatus status);

    long countByMemberAndTeamAndStatus(Member member, Team team, ApplicantStatus status);

}
