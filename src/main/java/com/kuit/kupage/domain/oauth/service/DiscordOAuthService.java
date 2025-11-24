package com.kuit.kupage.domain.oauth.service;

import com.kuit.kupage.common.auth.JwtTokenService;
import com.kuit.kupage.common.auth.TokenResponse;
import com.kuit.kupage.domain.memberRole.service.MemberRoleService;
import com.kuit.kupage.domain.oauth.DiscordApiType;
import com.kuit.kupage.domain.oauth.dto.DiscordInfoResponse;
import com.kuit.kupage.domain.oauth.dto.DiscordTokenResponse;
import com.kuit.kupage.domain.oauth.dto.LoginOrSignupResult;
import com.kuit.kupage.domain.role.Role;
import com.kuit.kupage.domain.role.dto.DiscordMemberResponse;
import com.kuit.kupage.domain.role.dto.DiscordRoleResponse;
import com.kuit.kupage.exception.KupageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static com.kuit.kupage.common.auth.AuthRole.GUEST;
import static com.kuit.kupage.common.response.ResponseCode.*;

@Slf4j
@Transactional
@Service
public class DiscordOAuthService {

    private static final long CODE_CACHE_TTL_MILLIS = Duration.ofMinutes(5).toMillis();
    private final Map<String, CodeCacheEntry> codeCache = new ConcurrentHashMap<>();

    private final RestClient restClient;
    private final MemberRoleService memberService;
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

    public DiscordOAuthService(RestClient.Builder builder, MemberRoleService memberService, JwtTokenService jwtTokenService) {
        this.restClient = builder
                .baseUrl("https://discord.com/api/v10")
                .build();
        this.memberService = memberService;
        this.jwtTokenService = jwtTokenService;
    }

    public LoginOrSignupResult requestToken(String code) {
        log.info("[requestToken] code={}", code);

        // 1) 재호출 빠른 처리: 같은 code로 이미 처리했다면 Discord 호출 없이 게스트 토큰 발급
        Long cachedMemberId = getCachedMemberId(code);
        if (cachedMemberId != null) {
            log.info("[requestToken] code 재호출 감지 → 캐시로 guestToken 재발급 (memberId={})", cachedMemberId);
            return new LoginOrSignupResult(cachedMemberId, List.of(GUEST.getValue()), issueGuestToken(cachedMemberId));
        }

        // 2) 최초 처리: Discord에 1회 교환 요청
        DiscordTokenResponse token = requestAccessToken(code);
        DiscordInfoResponse user = requestUserInfo(token.accessToken());

        Long memberId = memberService.getMemberIdByDiscordInfo(user);
        log.info("[requestToken] memberId = {}", memberId);

        // 3) 로그인/회원가입 기본 플로우 수행
        LoginOrSignupResult result = processLoginOrSignup(memberId, token, user);

        // 4) code → memberId 캐시
        log.info("[requestToken] code = {}, finalMemberId = {}", code, result.memberId());
        cacheCode(code, result.memberId());

        return result;
    }

    public List<DiscordRoleResponse> fetchGuildRoles() {
        List<DiscordRoleResponse> allRoleResponses = callDiscord(
                () -> restClient.get()
                        .uri("/guilds/" + GUILD_ID + "/roles")
                        .headers(headers -> headers.set("Authorization", getBotAuthorizationHeader()))
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {
                        }),
                DiscordApiType.GUILD_ROLES
        );

        // 봇을 제외하고 반환
        return Objects.requireNonNull(allRoleResponses).stream()
                .filter(roleResponse -> !roleResponse.isManaged())
                .toList();
    }

    public List<DiscordMemberResponse> fetchGuildMembers() {
        return callDiscord(
                () -> restClient.get()
                        .uri("/guilds/" + GUILD_ID + "/members?limit=1000")
                        .headers(headers -> headers.set("Authorization", getBotAuthorizationHeader()))
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {
                        }),
                DiscordApiType.GUILD_MEMBERS
        );
    }

    private DiscordTokenResponse requestAccessToken(String code) {
        log.info("[requestAccessToken] access token 요청, code={}", code);
        LinkedMultiValueMap<String, String> body = createBody(code);

        return callDiscord(
                () -> restClient.post()
                        .uri("/oauth2/token")
                        .headers(headers -> headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
                        .body(body)
                        .retrieve()
                        .toEntity(DiscordTokenResponse.class)
                        .getBody(),
                DiscordApiType.TOKEN
        );
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
        return callDiscord(
                () -> restClient.get()
                        .uri("/oauth2/@me")
                        .headers(headers -> headers.setBearerAuth(accessToken))
                        .retrieve()
                        .toEntity(DiscordInfoResponse.class)
                        .getBody(),
                DiscordApiType.USER_INFO
        );
    }

    private <T> T callDiscord(Supplier<T> caller, DiscordApiType type) {
        try {
            return caller.get();
        } catch (HttpClientErrorException e) {
            int status = e.getStatusCode().value();
            String responseBody = e.getResponseBodyAsString();
            log.error("[discord-api] type={}, status={}, body={}", type, status, responseBody, e);

            switch (type) {
                case TOKEN -> throw mapTokenError(status, responseBody);
                case USER_INFO -> throw mapUserInfoError(status);
                case GUILD_ROLES -> throw mapGuildRolesError(status);
                case GUILD_MEMBERS -> throw mapGuildMembersError(status);
                default -> throw new KupageException(DISCORD_OAUTH_CLIENT_ERROR);
            }
        } catch (Exception e) {
            log.error("[discord-api] type={} 알 수 없는 예외 발생", type, e);
            throw switch (type) {
                case TOKEN, USER_INFO -> new KupageException(DISCORD_OAUTH_SERVER_ERROR);
                case GUILD_ROLES -> new KupageException(DISCORD_ROLE_FETCH_FAIL);
                case GUILD_MEMBERS -> new KupageException(DISCORD_MEMBER_FETCH_FAIL);
            };
        }
    }

    private KupageException mapTokenError(int status, String responseBody) {
        // 400 Bad Request - 주로 code, redirect_uri, client 설정 문제
        if (status == 400) {
            if (responseBody != null) {
                // invalid_grant + Invalid "code" → 잘못되었거나 이미 사용/만료된 code
                if (responseBody.contains("invalid_grant") && responseBody.contains("Invalid \"code\"")) {
                    return new KupageException(DISCORD_OAUTH_INVALID_CODE);
                }
                // 기타 invalid_grant
                if (responseBody.contains("invalid_grant")) {
                    return new KupageException(DISCORD_OAUTH_INVALID_GRANT);
                }
                // 클라이언트 ID/Secret 또는 redirect 설정 문제
                if (responseBody.contains("invalid_client")) {
                    return new KupageException(DISCORD_OAUTH_INVALID_CLIENT);
                }
            }
            // 그 외 400 계열 토큰 요청 실패
            return new KupageException(DISCORD_OAUTH_BAD_REQUEST);
        }

        return mapCommonOAuthError(status);
    }

    private KupageException mapUserInfoError(int status) {
        // 유저 정보 조회 시에는 access token/권한 관련 오류 위주로 처리
        if (status == 400) {
            // 형식 오류 등 일반적인 잘못된 요청
            return new KupageException(DISCORD_OAUTH_BAD_REQUEST);
        }
        return mapCommonOAuthError(status);
    }


    private KupageException mapCommonOAuthError(int status) {
        // 401 Unauthorized - 보통 access token 또는 클라이언트 인증 정보가 잘못된 경우
        if (status == 401) {
            return new KupageException(DISCORD_OAUTH_UNAUTHORIZED);
        }
        // 403 Forbidden - 애플리케이션이 이 그랜트/리소스를 사용할 권한이 없는 경우
        if (status == 403) {
            return new KupageException(DISCORD_OAUTH_FORBIDDEN);
        }
        // 429 Too Many Requests - 레이트 리밋 초과
        if (status == 429) {
            return new KupageException(DISCORD_OAUTH_RATE_LIMITED);
        }
        // 나머지 4xx
        return new KupageException(DISCORD_OAUTH_CLIENT_ERROR);
    }

    private KupageException mapGuildRolesError(int status) {
        return mapGuildBotError(status, new KupageException(DISCORD_ROLE_FETCH_FAIL));
    }

    private KupageException mapGuildMembersError(int status) {
        return mapGuildBotError(status, new KupageException(DISCORD_MEMBER_FETCH_FAIL));
    }

    private KupageException mapGuildBotError(int status, KupageException defaultException) {
        if (status == 401) {
            return new KupageException(DISCORD_BOT_INVALID_TOKEN);
        }
        if (status == 403) {
            return new KupageException(DISCORD_BOT_FORBIDDEN);
        }
        return defaultException;
    }

    private LoginOrSignupResult processLoginOrSignup(Long memberId, DiscordTokenResponse response, DiscordInfoResponse userInfo) {
        if (memberId != null) {
            log.info("[processLoginOrSignup] 기존 회원 로그인 처리");
            List<String> roleNames = memberService.getCurrentMemberRolesByMemberId(memberId).stream()
                    .map(Role::getName)
                    .toList();
            return new LoginOrSignupResult(memberId, roleNames, memberService.updateToken(memberId, response));
        }
        log.info("[processLoginOrSignup] 신규 회원 회원가입 처리");
        return memberService.signup(response, userInfo);
    }

    private String getBotAuthorizationHeader() {
        return "Bot " + BOT_TOKEN;
    }

    private void cacheCode(String code, Long memberId) {
        if (code == null || memberId == null) return;
        long expiresAt = Instant.now().toEpochMilli() + CODE_CACHE_TTL_MILLIS;
        codeCache.put(code, new CodeCacheEntry(memberId, expiresAt));

        log.info("[cacheCode] cached code={} → memberId={}, expiresAt={}",
                code, memberId, Instant.ofEpochMilli(expiresAt));
    }

    private Long getCachedMemberId(String code) {
        if (code == null) return null;
        CodeCacheEntry entry = codeCache.get(code);
        if (entry == null) return null;
        if (entry.isExpired()) {
            codeCache.remove(code);
            return null;
        }
        return entry.memberId;
    }


    private TokenResponse issueGuestToken(Long memberId) {
        return jwtTokenService.generateGuestToken(memberId);
    }


    private static final class CodeCacheEntry {
        private final Long memberId;
        private final long expiresAtMs;

        private CodeCacheEntry(Long memberId, long expiresAtMs) {
            this.memberId = memberId;
            this.expiresAtMs = expiresAtMs;
        }

        private boolean isExpired() {
            return Instant.now().toEpochMilli() >= expiresAtMs;
        }
    }
}
