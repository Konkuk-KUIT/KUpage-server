package com.kuit.kupage.domain.teamMatch.repository;

import com.kuit.kupage.domain.teamMatch.TeamApplicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamApplicantRepository extends JpaRepository<TeamApplicant, Long> {

}
