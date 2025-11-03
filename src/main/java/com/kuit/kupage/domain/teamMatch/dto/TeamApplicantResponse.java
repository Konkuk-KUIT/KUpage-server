package com.kuit.kupage.domain.teamMatch.dto;

import com.kuit.kupage.domain.teamMatch.Part;

public record TeamApplicantResponse(
        String serviceName,
        String nameAndPart,
        Part part,
        String topicSummary,
        String mvpFeatures,
        ApplicantMap applicantMap
) {
}
