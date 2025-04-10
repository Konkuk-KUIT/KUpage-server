package com.kuit.kupage.domain.detail;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Grade {
    FIRST_YEAR("1학년"),
    SECOND_YEAR("2학년"),
    THIRD_YEAR("3학년"),
    FOURTH_YEAR("4학년"),
    FIFTH_YEAR("5학년"),
    SIXTH_YEAR("6학년"),
    LEAVE_OF_ABSENCE("휴학"),
    EXTENDED_SEMESTER("초과학기"),
    GRADUATED("졸업"),
    GRADUATION_DEFERRED("졸업 유예");

    private final String description;
}
