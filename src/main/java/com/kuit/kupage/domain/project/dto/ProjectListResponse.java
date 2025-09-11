package com.kuit.kupage.domain.project.dto;

import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.domain.project.entity.AppField;
import com.kuit.kupage.domain.project.entity.AppType;

import java.util.List;

public record ProjectListResponse(
        String title,
        String summary,
        Batch batch,
        AppType appType,
        List<AppField> appField,
        String thumbnailImagePath
) {
}
