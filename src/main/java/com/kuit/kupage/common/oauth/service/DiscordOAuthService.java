package com.kuit.kupage.common.oauth.service;

import com.kuit.kupage.common.auth.JwtTokenService;
import com.kuit.kupage.common.oauth.dto.DiscordInfoResponse;
import com.kuit.kupage.common.oauth.dto.DiscordTokenResponse;
import com.kuit.kupage.common.auth.AuthTokenResponse;
import com.kuit.kupage.domain.member.service.MemberService;
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
    private final JwtTokenService jwtTokenService;
    private final MemberService memberService;

    @Value("${spring.security.oauth2.client.registration.discord.redirect-uri}")
    private String REDIRECT_URI;

    @Value("${spring.security.oauth2.client.registration.discord.client-id}")
    private String CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.discord.client-secret}")
    private String CLIENT_SECRET;

    public DiscordOAuthService(RestClient.Builder builder, JwtTokenService jwtTokenService, MemberService memberService) {
        this.restClient = builder
                .baseUrl("https://discord.com/api/v10")
                .build();
        this.jwtTokenService = jwtTokenService;
        this.memberService = memberService;
    }

    public AuthTokenResponse requestToken(String code) {
        log.debug("[requestToken] access token 요청을 보내기 위해 필요한 code = {}", code);
        DiscordTokenResponse response = requestAccessToken(code);
        DiscordInfoResponse userInfo = requestUserInfo(response.getAccessToken());
        Long memberId = memberService.lookupMemberId(userInfo);
        return processLoginOrSignup(memberId, response, userInfo);
    }

    private DiscordTokenResponse requestAccessToken(String code) {
        log.debug("[requestAccessToken] access token 요청");
        LinkedMultiValueMap<String, String> body = createBody(code);
        DiscordTokenResponse response = restClient.post()
                .uri("/oauth2/token")
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
                .body(body)
                .retrieve()
                .toEntity(DiscordTokenResponse.class)
                .getBody();
        log.debug("[requestAccessToken] 토큰 응답 = {}", response);
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
        log.debug("[requestUserInfo] 사용자 정보 요청시 필요한 access token = {}", accessToken);
        DiscordInfoResponse response = restClient.get()
                .uri("/oauth2/@me")
                .headers(headers -> {
                    headers.setBearerAuth(accessToken);
                })
                .retrieve()
                .toEntity(DiscordInfoResponse.class)
                .getBody();
        log.debug("[requestUserInfo] 사용자 정보 응답 = {}", response);
        return response;
    }


    private AuthTokenResponse processLoginOrSignup(Long memberId, DiscordTokenResponse response, DiscordInfoResponse userInfo) {
        if (memberId != null){
            // 기존 회원 : AuthToken 발급&홈으로 리다이렉트 (로그인 처리)
            log.debug("[processLoginOrSignup] 기존 회원 로그인 처리");
            return memberService.updateToken(memberId, response);
        }
        // 신규 회원 : 추가 정보 받기 -> 회원가입 처리 -> AuthToken 발급&홈으로 리다이렉트 (로그인 처리)
        log.debug("[processLoginOrSignup] 신규 회원 회원가입 처리");
        return memberService.signup(response, userInfo);
    }
}
