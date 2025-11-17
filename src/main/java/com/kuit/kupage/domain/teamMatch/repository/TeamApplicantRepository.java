package com.kuit.kupage.domain.teamMatch.repository;

import com.kuit.kupage.domain.teamMatch.TeamApplicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamApplicantRepository extends JpaRepository<TeamApplicant, Long> {

    @Query
    Optional<TeamApplicant> findByMemberId(Long memberId);
}
