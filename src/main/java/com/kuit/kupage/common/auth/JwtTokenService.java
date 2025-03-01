package com.kuit.kupage.common.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Getter
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class JwtTokenService {

    @Value("${secret.jwt.key}")
    private String secretKey;

    @Value("${secret.jwt.access.expiration}")
    private long accessTokenExpiration;

    @Value("${secret.jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    public AuthTokenResponse generateTokens(Long memberId) {
        // TODO. 우리 서버용 토큰 발급해서 SignupResponse 생성, 반환
        return null;
    }
}
