package com.kuit.kupage.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@ToString
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class DiscordToken {
    @Column(name = "discord_access_token")
    private String accessToken;

    @Column(name = "discord_refresh_token")
    private String refreshToken;

    @Column(name = "discord_expires_in")
    private Long expiresIn;

    public DiscordToken(String accessToken, String refreshToken, Long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

}
