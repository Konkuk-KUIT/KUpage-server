package com.kuit.kupage.domain.project.dto;

import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.domain.project.entity.AppType;
import lombok.Builder;

@Builder
public record ProjectInfo (
        Batch batch,
        AppType appType,
        String[] categories,
        String projectName,
        String[] members,
        String[] tools
) {
}
