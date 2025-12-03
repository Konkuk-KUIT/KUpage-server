package com.kuit.kupage.domain.teamMatch.repository;

import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.domain.teamMatch.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findAllByBatch(Batch batch);

    @Query("SELECT DISTINCT t FROM Team t LEFT JOIN FETCH t.teamApplicants ta LEFT JOIN FETCH ta.member WHERE t.batch =:batch")
    List<Team> findAllWithTeamApplicantAndMemberByBatch(@Param("batch") Batch batch);

    @Query("SELECT DISTINCT t FROM Team t LEFT JOIN FETCH t.teamApplicants ta LEFT JOIN FETCH ta.member WHERE t.id =:teamId")
    Optional<Team> findAllWithTeamApplicantAndMemberById(@Param("teamId") Long teamId);

    @Query("select DISTINCT t From Team t LEFT JOIN FETCH t.teamApplicants ta LEFT JOIN FETCH ta.member where t.ownerId =:ownerId and t.batch =:batch")
    Optional<Team> findByOwnerIdAndBatch(@Param("ownerId")Long ownerId, @Param("batch") Batch batch);

    @Query("SELECT DISTINCT t FROM Team t JOIN FETCH t.teamApplicants ta JOIN FETCH ta.member m WHERE t.batch =:batch and m.id = :memberId")
    List<Team> findAllTeamsByMemberIdAndBatch(@Param("memberId") Long memberId, @Param("batch") Batch batch);

}
