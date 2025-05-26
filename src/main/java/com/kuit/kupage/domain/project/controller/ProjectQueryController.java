package com.kuit.kupage.domain.project.controller;

import com.kuit.kupage.common.response.BaseResponse;
import com.kuit.kupage.common.response.PagedResponse;
import com.kuit.kupage.domain.project.dto.ProjectListResponse;
import com.kuit.kupage.domain.project.service.ProjectService;
import com.kuit.kupage.domain.project.entity.Project;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "프로젝트 조회 Controller", description = "프로젝트 조회 관련 Controller 입니다.")
public class ProjectQueryController {

    private final ProjectService projectService;
    private final int PROJECT_COUNT_PER_PAGE = 9;

    public ProjectQueryController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Operation(summary = "프로젝트 목록 조회", description = "프로젝트 목록을 기수별로 조회합니다")
    @GetMapping("/projects")
    public BaseResponse<PagedResponse<ProjectListResponse>> getProjects(@RequestParam("batch") ProjectQueryBatch batch, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, PROJECT_COUNT_PER_PAGE, Sort.by("createdAt").descending());
        Page<Project> projects = projectService.searchProjectsByBatch(pageable, batch.toString());
        List<ProjectListResponse> responses = projects.stream()
                .map(p -> new ProjectListResponse(
                        p.getName(),
                        p.getSummary(),
                        p.getBatch(),
                        p.getAppType(),
                        p.getAppFields(),
                        p.getMainImagePath()))
                .toList();
        return new BaseResponse<>(PagedResponse.of(responses, projects));
    }
}
