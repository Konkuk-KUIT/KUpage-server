package com.kuit.kupage.domain.project.controller;


import com.kuit.kupage.common.response.BaseResponse;
import com.kuit.kupage.domain.project.dto.ProjectResponse;
import com.kuit.kupage.domain.project.service.ProjectQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProjectQueryController {

    private final ProjectQueryService projectQueryService;

    @GetMapping("/projects/{projectId}")
    public BaseResponse<ProjectResponse> getProject(@PathVariable(name = "projectId") Long projectId) {
        return new BaseResponse<>(projectQueryService.getProject(projectId));
    }


}
