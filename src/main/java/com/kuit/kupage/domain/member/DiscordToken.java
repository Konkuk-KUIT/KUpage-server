package com.kuit.kupage.domain.member;

import com.kuit.kupage.common.oauth.dto.DiscordTokenResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Getter
@Slf4j
@ToString
@Embeddable
@NoArgsConstructor
public class DiscordToken {
    @Column(name = "discord_access_token")
    private String accessToken;

    @Column(name = "discord_refresh_token")
    private String refreshToken;

    @Column(name = "discord_expires_in")
    private Long expiresIn;

    public DiscordToken(DiscordTokenResponse response) {
        this.accessToken = response.getAccessToken();
        this.refreshToken = response.getRefreshToken();
        this.expiresIn = response.getExpiresIn();
    }

    public DiscordToken(String accessToken, String refreshToken, Long expiresIn) {
        DiscordToken discordToken = new DiscordToken();
        discordToken.update(accessToken, refreshToken, expiresIn);
    }

    public void update(String accessToken, String refreshToken, Long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscordToken that = (DiscordToken) o;
        return Objects.equals(getAccessToken(), that.getAccessToken()) && Objects.equals(getRefreshToken(), that.getRefreshToken()) && Objects.equals(getExpiresIn(), that.getExpiresIn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAccessToken(), getRefreshToken(), getExpiresIn());
    }
}
