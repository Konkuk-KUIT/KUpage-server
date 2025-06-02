package com.kuit.kupage.domain.project.dto;

import lombok.Builder;

@Builder
public record ProjectResponse(
        Long projectId,
        ProjectInfo projectInfo,
        ProjectDetail projectDetail,
        ReviewDetails reviewDetails
) {
}
