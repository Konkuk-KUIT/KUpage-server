package com.kuit.kupage.domain.detail.dto;

import com.kuit.kupage.domain.detail.Detail;
import com.kuit.kupage.domain.detail.Grade;
import com.kuit.kupage.domain.member.Member;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

public record MyPageResponse(
        String name,
        String profileImage,
        String studentNumber,
        String departName,
        Grade grade,
        String githubId,
        String email,
        String phoneNumber,
        LocalDate birthday,
        List<String> roles
) {
    public static MyPageResponse from(Member member, List<String> roles) {
        Detail detail = member.getDetail();
        return new MyPageResponse(
                member.getName(),
                member.getProfileImage(),
                detail.getStudentNumber(),
                detail.getDepartName(),
                detail.getGrade(),
                detail.getGithubId(),
                detail.getEmail(),
                detail.getPhoneNumber(),
                detail.getBirthday(),
                roles
        );
    }
}
