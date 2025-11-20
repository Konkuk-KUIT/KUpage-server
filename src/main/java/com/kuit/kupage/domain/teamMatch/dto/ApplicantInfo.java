package com.kuit.kupage.domain.teamMatch.dto;

import com.kuit.kupage.domain.teamMatch.Part;

import java.time.LocalDateTime;

public record ApplicantInfo(Long memberId, String applicantMemberNameAndPart, Part part, String appliedTime, ApplicantDetail applicantDetail) {
}
