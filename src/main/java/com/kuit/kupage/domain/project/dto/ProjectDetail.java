package com.kuit.kupage.domain.project.dto;

import lombok.Builder;

@Builder
public record ProjectDetail (
        String mainImagePath,
        String detail_file_url,
        String projectDescription
){
}
