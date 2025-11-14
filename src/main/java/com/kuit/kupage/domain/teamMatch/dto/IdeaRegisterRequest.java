package com.kuit.kupage.domain.teamMatch.dto;

import com.kuit.kupage.domain.project.entity.AppType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record IdeaRegisterRequest(
        @NotBlank
        @Size(max = 100)
        String serviceName,

        AppType appType,

        @Size(max = 500)
        String topicSummary,

        @Size(max = 500)
        String imageUrl,

        @Size(max = 500)
        String serviceIntroFile,

        @Size(max = 1000)
        String featureRequirements,

        @Size(max = 1000)
        String preferredDeveloper
) {
}
