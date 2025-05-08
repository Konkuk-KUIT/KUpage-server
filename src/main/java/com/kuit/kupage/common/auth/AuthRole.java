package com.kuit.kupage.common.auth;

import lombok.Getter;

@Getter
public enum AuthRole {

    GUEST("GUEST"), // 디스코드 인증 후 회원가입 시점까지 사용
    DEFAULT("DEFAULT"), // 회원가입 완료 후 role 업데이트 전까지 사용
    MEMBER("MEMBER"), // 일반 부원
    TUTOR("TUTOR"), // 튜터
    ADMIN("ADMIN"); // 운영진

    AuthRole(String value) {
        this.value = value;
        this.role = PREFIX + value;
    }

    private static final String PREFIX = "ROLE_";
    private final String value;
    private final String role;

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean isTutor() {
        return this == TUTOR;
    }

}
