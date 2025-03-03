package com.kuit.kupage.domain.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DiscordInfoResponse {
    private List<String> scopes;
    private Instant expires;
    @JsonProperty("user")
    private UserResponse userResponse;

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class UserResponse {
        private String id;
        private String username;          // 디스코드의 로그인 ID 역할
        private String avatar;

        @JsonProperty("global_name")
        private String globalName;        // 서버에 관계없이 보이는 닉네임

        @JsonProperty("public_flags")
        private int publicFlags;          // 사용자 배지
    }
}
