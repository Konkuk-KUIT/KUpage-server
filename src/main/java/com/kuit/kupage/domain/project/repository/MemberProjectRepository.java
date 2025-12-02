package com.kuit.kupage.domain.project.repository;

import com.kuit.kupage.domain.project.domain.MemberProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberProjectRepository extends JpaRepository<MemberProject, Long> {

    @Query("""
        SELECT mp FROM MemberProject mp
        JOIN FETCH mp.member m
        where mp.project.id = :projectId
    """)
    List<MemberProject> findAllByProjectIdWithMember(@Param("projectId") Long projectId);
}
