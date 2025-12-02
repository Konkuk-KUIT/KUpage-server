package com.kuit.kupage.domain.teamMatch.dto;

import com.kuit.kupage.domain.project.domain.AppType;

public record TeamApplicantResponse(
        Long teamId,
        String serviceName,
        String nameAndPart,
        AppType appType,
        String topicSummary,
        String mvpFeatures,
        ApplicantMap applicantMap
) {
}
