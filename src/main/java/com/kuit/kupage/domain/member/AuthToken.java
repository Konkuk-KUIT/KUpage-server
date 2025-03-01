package com.kuit.kupage.domain.member;

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
public class AuthToken {
    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    public AuthToken(String accessToken, String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthToken authToken = (AuthToken) o;
        return Objects.equals(getAccessToken(), authToken.getAccessToken()) && Objects.equals(getRefreshToken(), authToken.getRefreshToken());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAccessToken(), getRefreshToken());
    }
}
