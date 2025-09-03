package com.kuit.kupage.domain.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Batch {

    FIRST("1st"),
    SECOND("2nd"),
    THIRD("3rd"),
    FOURTH("4th"),
    FIFTH("5th"),
    SIXTH("6th"),
    UNKNOWN("기수를 알 수 없습니다.");


    private final String description;


    public static Batch parseBatch(String rawRoleName) {
        String roleName = rawRoleName.toLowerCase();
        if (roleName.contains("1기")) {
            return FIRST;
        }
        if (roleName.contains("2기")) {
            return SECOND;
        }
        for (Batch batch : Batch.values()) {
            if (roleName.contains(batch.description)) {
                return batch;
            }
        }
        return UNKNOWN;
    }
}
