package com.kuit.kupage.domain.project.repository;

import com.kuit.kupage.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
