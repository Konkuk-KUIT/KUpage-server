package com.kuit.kupage.domain.detail.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kuit.kupage.domain.detail.Grade;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record MyPageUpdateRequest(
        @Size(min = 2) String name,
        @NotBlank String studentNumber,
        @NotBlank String departName,
        @NotNull Grade grade,
        String githubId,
        @Email String email,
        @Pattern(regexp = "010-\\d{4}-\\d{4}") String phoneNumber,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthday
) {
}
