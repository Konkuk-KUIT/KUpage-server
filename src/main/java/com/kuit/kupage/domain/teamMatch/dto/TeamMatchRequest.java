package com.kuit.kupage.domain.teamMatch.dto;

import com.kuit.kupage.domain.teamMatch.Part;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record TeamMatchRequest(
        @NotBlank(message = "이름은 필수 입력 항목입니다.")
        @Size(max = 50, message = "이름은 최대 50자까지 입력 가능합니다.")
        String name,

        @Size(max = 20, message = "학번은 최대 20자까지 입력 가능합니다.")
        String studentId,

        @NotNull(message = "지원 파트는 필수 입력 항목입니다.")
        Part appliedPart,

        @Size(max = 1000, message = "지원 동기는 최대 1000자까지 입력 가능합니다.")
        String motivation,

        @Size(max = 500, message = "포트폴리오 URL은 최대 500자까지 입력 가능합니다.")
        @URL(message = "유효한 URL 형식이어야 합니다.")
        String portfolioUrl,

        @Size(max = 5000, message = "추가 질문 1의 답변은 최대 5000자까지 입력 가능합니다.")
        String additionalAnswer1,

        @Size(max = 5000, message = "추가 질문 2의 답변은 최대 5000자까지 입력 가능합니다.")
        String additionalAnswer2
) { }
