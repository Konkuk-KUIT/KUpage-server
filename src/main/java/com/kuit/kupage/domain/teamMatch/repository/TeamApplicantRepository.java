package com.kuit.kupage.domain.teamMatch.repository;

import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.teamMatch.ApplicantStatus;
import com.kuit.kupage.domain.teamMatch.Team;
import com.kuit.kupage.domain.teamMatch.TeamApplicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamApplicantRepository extends JpaRepository<TeamApplicant, Long> {
    @Query("select ta.slotNo from TeamApplicant ta where ta.member = :member and ta.status = :status and ta.batch = :batch")
    List<Integer> findSlotNosByMemberAndStatus(Member member, ApplicantStatus status, Batch batch);

    @Query("select count(ta) from TeamApplicant ta where ta.member = :member and ta.status = :status and ta.batch = :batch")
    long countByMemberAndStatusAndBatch(Member member, ApplicantStatus status, Batch batch);

    long countByMemberAndTeamAndStatus(Member member, Team team, ApplicantStatus status);

    List<TeamApplicant> findByMember_IdAndTeam_Batch(Long memberId, Batch batch);

    @Query("SELECT ta FROM TeamApplicant ta WHERE ta.applicantName =:applicantName")
    Optional<TeamApplicant> findByApplicantName(@Param("applicantName") String applicantName);
}
