package com.kuit.kupage.domain.project.repository;

import com.kuit.kupage.domain.project.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.project.id = :projectId")
    List<Review> findByProjectId(@Param("projectId")Long projectId);
}
