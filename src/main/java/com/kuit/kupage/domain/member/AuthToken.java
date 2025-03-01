package com.kuit.kupage.domain.member;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@Embeddable
@NoArgsConstructor
public class AuthToken {
    private String accessToken;
    private String refreshToken;

    public void update(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
