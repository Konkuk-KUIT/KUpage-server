package com.kuit.kupage.domain.teamMatch.dto;

import com.kuit.kupage.domain.teamMatch.Part;

import java.util.List;
import java.util.Map;

public record ApplicantMap(Map<Part, List<ApplicantInfo>> applicants) {
}
