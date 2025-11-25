package com.kuit.kupage.domain.teamMatch.repository;

import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.teamMatch.ApplicantStatus;
import com.kuit.kupage.domain.teamMatch.TeamApplicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamApplicantRepository extends JpaRepository<TeamApplicant, Long> {

    @Query("""
            select COUNT(ta)
            from TeamApplicant ta join fetch ta.team t
            where ta.member = :member and t.batch = :batch and ta.status = :status
           """)
    long countByMemberAndBatchAndStatus(@Param("member") Member member,
                                        @Param("batch") Batch batch,
                                        @Param("status") ApplicantStatus status);
}
