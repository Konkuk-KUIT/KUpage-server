package com.kuit.kupage.domain.teamMatch.dto;

import com.kuit.kupage.domain.project.entity.AppType;

public record TeamApplicantOverviewDto(
        Long teamId,
        String serviceName,
        String nameAndPart,
        AppType appType,
        String topicSummary,
        int AndroidApplicantNum,
        int iosApplicantNum,
        int webApplicantNum,
        int serverApplicantNum,
        int designApplicantNum) {
}
