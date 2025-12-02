package com.kuit.kupage.domain.project.repository;

import com.kuit.kupage.domain.project.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c JOIN FETCH c.project WHERE c.project.id = :projectId")
    List<Category> findAllByProjectId(@Param("projectId") Long projectId);
}
