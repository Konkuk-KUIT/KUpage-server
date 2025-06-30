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
    SIXTH("6th");

    private final String description;


    public static Batch parseBatch(String batchStr) {
        return switch (batchStr.toLowerCase()) {
            case "1st" -> Batch.FIRST;
            case "2nd" -> Batch.SECOND;
            case "3rd" -> Batch.THIRD;
            case "4th" -> Batch.FOURTH;
            case "5th" -> Batch.FIFTH;
            case "6th" -> Batch.SIXTH;
            default -> throw new IllegalArgumentException("알 수 없는 기수: " + batchStr);
        };
    }
}
