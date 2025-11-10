package com.kuit.kupage.domain.teamMatch.dto;

import com.kuit.kupage.domain.teamMatch.Part;

public record ApplicantInfo(String applicantMemberNameAndPart, Part part, ApplicantDetail applicantDetail) {
}
