package com.kuit.kupage.domain.project.repository;

import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.domain.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findAllByBatch(Pageable pageable, Batch batch);
}
