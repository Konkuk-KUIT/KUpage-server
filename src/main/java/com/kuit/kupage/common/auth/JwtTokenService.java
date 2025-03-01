package com.kuit.kupage.common.auth;

import com.kuit.kupage.domain.member.AuthToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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

    private final static String ACCESS = "access";
    private final static String REFRESH = "refresh";

    public AuthTokenResponse generateTokens(Long memberId) {
        log.info("[generateTokens] 토큰을 발급할 회원 id = {}", memberId);
        final Claims claims = Jwts.claims();
        claims.put("memberId", memberId);
        String accessToken = generateToken(claims, accessTokenExpiration, ACCESS);
        String refreshToken = generateToken(claims, refreshTokenExpiration, REFRESH);
        log.info("[generateTokens] 발급한 토큰 정보 access token = {}, refresh token = {}", accessToken, refreshToken);
        return new AuthTokenResponse(accessToken, refreshToken);
    }

    private String generateToken(Claims claims, long expiration, String type) {
        claims.put("type", type);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}
