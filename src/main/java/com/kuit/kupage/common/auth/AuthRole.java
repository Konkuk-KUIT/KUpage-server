package com.kuit.kupage.common.auth;

import lombok.Getter;

@Getter
public enum AuthRole {

    GUEST("GUEST"),
    MEMBER("MEMBER"),
    ADMIN("ADMIN");

    AuthRole(String value) {
        this.value = value;
        this.role = PREFIX + value;
    }

    private final String PREFIX = "ROLE_";
    private final String value;
    private final String role;
}
