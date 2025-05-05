package com.kuit.kupage.domain.oauth.service;

import com.kuit.kupage.common.auth.JwtTokenService;
import com.kuit.kupage.common.auth.TokenResponse;
import com.kuit.kupage.domain.member.service.MemberService;
import com.kuit.kupage.domain.oauth.dto.DiscordInfoResponse;
import com.kuit.kupage.domain.oauth.dto.DiscordTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Slf4j
@Transactional
@Service
public class DiscordOAuthService {
    private final RestClient restClient;
    private final MemberService memberService;
    private final JwtTokenService jwtTokenService;

    @Value("${spring.security.oauth2.client.registration.discord.redirect-uri}")
    private String REDIRECT_URI;

    @Value("${spring.security.oauth2.client.registration.discord.client-id}")
    private String CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.discord.client-secret}")
    private String CLIENT_SECRET;

    public DiscordOAuthService(RestClient.Builder builder, MemberService memberService, JwtTokenService jwtTokenService) {
        this.restClient = builder
                .baseUrl("https://discord.com/api/v10")
                .build();
        this.memberService = memberService;
        this.jwtTokenService = jwtTokenService;
    }

    public TokenResponse requestToken(String code) {
        log.info("[requestToken] access token 요청을 보내기 위해 필요한 code = {}", code);
        DiscordTokenResponse response = requestAccessToken(code);
        DiscordInfoResponse userInfo = requestUserInfo(response.accessToken());     // merge 후 HttpClientErrorException 예외처리 필요
        Long memberId = memberService.getMemberIdByDiscordInfo(userInfo);
        return processLoginOrSignup(memberId, response, userInfo);
    }

    private DiscordTokenResponse requestAccessToken(String code) {
        log.info("[requestAccessToken] access token 요청");
        LinkedMultiValueMap<String, String> body = createBody(code);
        DiscordTokenResponse response = restClient.post()
                .uri("/oauth2/token")
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
                .body(body)
                .retrieve()
                .toEntity(DiscordTokenResponse.class)
                .getBody();
        log.info("[requestAccessToken] 토큰 응답 = {}", response);
        return response;
    }

    private LinkedMultiValueMap<String, String> createBody(String code) {
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", CLIENT_ID);
        body.add("client_secret", CLIENT_SECRET);
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", REDIRECT_URI);
        return body;
    }

    private DiscordInfoResponse requestUserInfo(String accessToken) {
        log.info("[requestUserInfo] 사용자 정보 요청시 필요한 access token = {}", accessToken);
        DiscordInfoResponse response = restClient.get()
                .uri("/oauth2/@me")
                .headers(headers -> {
                    headers.setBearerAuth(accessToken);
                })
                .retrieve()
                .toEntity(DiscordInfoResponse.class)
                .getBody();
        log.info("[requestUserInfo] 사용자 정보 응답 = {}", response);
        return response;
    }


    private TokenResponse processLoginOrSignup(Long memberId, DiscordTokenResponse response, DiscordInfoResponse userInfo) {
        if (memberId != null) {
            log.debug("[processLoginOrSignup] 기존 회원 로그인 처리");
            return memberService.updateToken(memberId, response);
        }
        log.debug("[processLoginOrSignup] 신규 회원 회원가입 처리");
        return jwtTokenService.generateGuestToken(userInfo);
    }
}
