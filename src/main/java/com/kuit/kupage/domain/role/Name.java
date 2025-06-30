package com.kuit.kupage.domain.role;

public enum Name {
    ADMIN, PART_LEADER, TUTOR, MEMBER;

    public static Name parseName(String nameStr) {
        return switch (nameStr) {
            case "부원" -> Name.MEMBER;
            case "튜터" -> Name.TUTOR;
            case "파트장" -> Name.PART_LEADER;
            case "운영진" -> Name.ADMIN;
            default -> throw new IllegalArgumentException("알 수 없는 역할 이름: " + nameStr);
        };
    }
}
