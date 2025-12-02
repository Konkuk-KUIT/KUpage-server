package com.kuit.kupage.domain.teamMatch.dto;

import com.kuit.kupage.domain.project.domain.AppType;

public record TeamOverviewDto(
        Long teamId,
        String serviceName,
        String topicSummary,
        String ownerNameAndPart,
        AppType appType) {
}
