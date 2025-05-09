package com.kuit.kupage.domain.role;

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

}
