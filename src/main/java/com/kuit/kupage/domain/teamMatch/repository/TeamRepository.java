package com.kuit.kupage.domain.teamMatch.repository;

import com.kuit.kupage.domain.teamMatch.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
}
