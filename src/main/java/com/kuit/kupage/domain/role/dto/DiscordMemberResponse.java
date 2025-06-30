package com.kuit.kupage.domain.role.dto;

import lombok.Data;
import java.util.List;

@Data
public class DiscordMemberResponse {
    private DiscordUser user;
    private String nick;            // 서버 내에서 설정한 닉네임 (없을 경우 null 또는 username 사용)
    private List<String> roles;     // 서버에서 해당 사용자가 가진 역할 ID 목록 (role ID는 Discord 서버 내부 고유 ID)
    private String joined_at;       // 서버에 가입한 날짜 및 시간 (ISO-8601 형식 문자열일 가능성 높음)

    @Data
    public static class DiscordUser {
        private String id;                  // Discord 사용자 고유 ID (정수형 문자열)
        private String username;            // Discord 사용자 이름 (예: "rbqks529")
        private String avatar;              // 사용자 프로필 이미지 해시 (CDN URL과 결합해 이미지 조회 가능)
        private boolean bot;                // 봇에 의해 관리되는 계정 여부 (true = Discord 봇)
    }
}