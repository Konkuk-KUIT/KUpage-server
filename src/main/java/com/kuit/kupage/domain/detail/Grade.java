package com.kuit.kupage.domain.detail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
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
    GRADUATED("졸업생"),
    GRADUATION_DEFERRED("졸업유예");

    private final String description;

    @JsonCreator
    public static Grade from(String input) throws InvalidFormatException{
        for (Grade grade : Grade.values()) {
            if (grade.description.equals(input)) {
                return grade;
            }
        }
        throw new InvalidFormatException(null, "지원하지 않는 학년입니다.", input, Grade.class);
    }

    @JsonValue
    public String toJson() {
        return description; // 응답에는 "1학년"처럼 description이 나가게 됨
    }
}
