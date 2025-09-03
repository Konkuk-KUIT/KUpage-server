package com.kuit.kupage.domain.oauth.service;

import com.kuit.kupage.common.auth.JwtTokenService;
import com.kuit.kupage.common.auth.TokenResponse;
import com.kuit.kupage.domain.member.service.MemberService;
import com.kuit.kupage.domain.oauth.dto.DiscordInfoResponse;
import com.kuit.kupage.domain.oauth.dto.DiscordTokenResponse;
import com.kuit.kupage.domain.role.dto.DiscordMemberResponse;
import com.kuit.kupage.domain.role.dto.DiscordRoleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

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

    @Value("${discord.guild-id}")
    private String GUILD_ID;

    @Value("${discord.bot-token}")
    private String BOT_TOKEN;

    public DiscordOAuthService(RestClient.Builder builder, MemberService memberService, JwtTokenService jwtTokenService) {
        this.restClient = builder
                .baseUrl("https://discord.com/api/v10")
                .build();
        this.memberService = memberService;
        this.jwtTokenService = jwtTokenService;
    }

    public TokenResponse requestToken(String code) {
        log.debug("[requestToken] access token 요청을 보내기 위해 필요한 code = {}", code);
        DiscordTokenResponse response = requestAccessToken(code);
        DiscordInfoResponse userInfo = requestUserInfo(response.accessToken());     // merge 후 HttpClientErrorException 예외처리 필요
        Long memberId = memberService.getMemberIdByDiscordInfo(userInfo);
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


    private TokenResponse processLoginOrSignup(Long memberId, DiscordTokenResponse response, DiscordInfoResponse userInfo) {
        if (memberId != null) {
            log.debug("[processLoginOrSignup] 기존 회원 로그인 처리");
            return memberService.updateToken(memberId, response);
        }
        log.debug("[processLoginOrSignup] 신규 회원 회원가입 처리");
        return memberService.signup(response, userInfo);
//        return jwtTokenService.generateGuestToken(userInfo);
    }

    public List<DiscordRoleResponse> fetchGuildRoles() {
        try {
            List<DiscordRoleResponse> allRoleResponses = restClient.get()
                    .uri("/guilds/" + GUILD_ID + "/roles")
                    .headers(headers -> headers.set("Authorization", "Bot " + BOT_TOKEN))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            // 봇을 제외하고 반환
            return Objects.requireNonNull(allRoleResponses).stream()
                    .filter(roleResponse -> !roleResponse.isManaged())
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("디스코드 역할 조회 실패", e);
        }
    }

    public List<DiscordMemberResponse> fetchGuildMembers() {
        try {
            List<DiscordMemberResponse> body = restClient.get()
                    .uri("/guilds/" + GUILD_ID + "/members?limit=1000")
                    .headers(headers -> headers.set("Authorization", "Bot " + BOT_TOKEN))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
//            for (DiscordMemberResponse discordMemberResponse : body) {
//                log.debug("유저: {}#{} | 역할 수: {}",
//                        discordMemberResponse.getUser().getUsername(),
//                        discordMemberResponse.getUser().getDiscriminator(),
//                        discordMemberResponse.getRoles().size());
//            }
            return body;
        } catch (Exception e) {
            throw new RuntimeException("디스코드 멤버 조회 실패", e);
        }
    }
}
