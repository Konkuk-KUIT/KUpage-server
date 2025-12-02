package com.kuit.kupage.domain.project.service;

import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.domain.project.domain.Project;
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

    public Page<Project> searchProjectsByBatch(Pageable pageable, Batch batch) {
        if(batch.equals(Batch.ALL)) {
            return projectRepository.findAll(pageable);
        }

        return projectRepository.findAllByBatch(pageable, batch);
    }
}
