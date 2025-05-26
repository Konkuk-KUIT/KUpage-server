package com.kuit.kupage.domain.project.service;

import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.domain.project.entity.Project;
import com.kuit.kupage.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    public Page<Project> searchProjectsByBatch(Pageable pageable, String batch) {
        if(batch.equals("ALL")) {
            return projectRepository.findAll(pageable);
        }

        return projectRepository.findAllByBatch(pageable, Batch.valueOf(batch));
    }
}
