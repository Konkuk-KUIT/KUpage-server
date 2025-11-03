package com.kuit.kupage.domain.teamMatch.dto;

import com.kuit.kupage.domain.teamMatch.Part;

public record TeamApplicantOverviewDto(
        String serviceName,
        String nameAndPart,
        Part part,
        String topicSummary,
        int androidApplicantNum,
        int iosApplicantNum,
        int webApplicantNum,
        int serverApplicantNum,
        int designApplicantNum) {
}
