package com.kuit.kupage.domain.detail.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kuit.kupage.domain.detail.Grade;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SignupRequest {
    @Size(min = 2)
    private String name;
    @NotNull
    private Long studentNumber;
    @NotNull
    private Grade grade;
    @NotBlank
    private String college;
    @NotBlank
    private String departName;
    private String githubId;
    @Email
    private String email;
    @Pattern(regexp = "010-\\d{4}-\\d{4}")
    private String phoneNumber;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}
