package com.kuit.kupage.domain.member;

import com.kuit.kupage.common.oauth.dto.DiscordTokenResponse;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@Embeddable
@NoArgsConstructor
public class DiscordToken {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;

    public DiscordToken(DiscordTokenResponse response) {
        this.accessToken = response.getAccessToken();
        this.refreshToken = response.getRefreshToken();
        this.expiresIn = response.getExpiresIn();
    }
}
