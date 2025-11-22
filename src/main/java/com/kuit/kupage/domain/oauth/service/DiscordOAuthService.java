package com.kuit.kupage.domain.oauth.service;

import com.kuit.kupage.common.auth.JwtTokenService;
import com.kuit.kupage.common.auth.TokenResponse;
import com.kuit.kupage.domain.memberRole.service.MemberRoleService;
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

        try {
            // 2) 최초 처리: Discord에 1회 교환 요청
            DiscordTokenResponse token = requestAccessToken(code);
            DiscordInfoResponse user = requestUserInfo(token.accessToken());

            Long memberId = memberService.getMemberIdByDiscordInfo(user);
            log.info("[requestToken] memberId = {}", memberId);

            // 2) 로그인/회원가입 기본 플로우 수행
            LoginOrSignupResult result = processLoginOrSignup(memberId, token, user);

            // 3) code → memberId 캐시
            log.info("[requestToken] code = {}, finalMemberId = {}", code, result.memberId());
            cacheCode(code, result.memberId());

            return result;

        } catch (HttpClientErrorException.BadRequest e) {
            // 4) invalid_grant(동일 code 재사용 등) 이면 캐시 폴백
            if (isInvalidGrant(e)) {
                Long memberId = getCachedMemberId(code);
                if (memberId != null) {
                    log.info("[requestToken] invalid_grant but cached → guestToken 재발급 (memberId={})", memberId);
                    return new LoginOrSignupResult(memberId, List.of(GUEST.getValue()), issueGuestToken(memberId));
                }
            }
            throw e;
        }
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
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("[fetchGuildRoles] Bot 토큰이 잘못됨 (401 Unauthorized). body={}",
                    e.getResponseBodyAsString(), e);
            throw new KupageException(DISCORD_BOT_INVALID_TOKEN);

        } catch (HttpClientErrorException.Forbidden e) {
            log.error("[fetchGuildRoles] 권한 부족 (403 Forbidden). body={}",
                    e.getResponseBodyAsString(), e);
            throw new KupageException(DISCORD_BOT_FORBIDDEN);
        } catch (HttpClientErrorException e) {
            // 4xx인데 다른 상태코드인 경우
            log.error("[fetchGuildRoles] Discord 4xx 오류 발생. status={}, body={}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new KupageException(DISCORD_ROLE_FETCH_FAIL);

        } catch (Exception e) {
            log.error("[fetchGuildRoles] 알 수 없는 예외 발생", e);
            throw new KupageException(DISCORD_ROLE_FETCH_FAIL);
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
            return body;
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new KupageException(DISCORD_BOT_INVALID_TOKEN);
        } catch (HttpClientErrorException.Forbidden e) {
            throw new KupageException(DISCORD_BOT_FORBIDDEN);
        } catch (Exception e) {
            throw new KupageException(DISCORD_MEMBER_FETCH_FAIL);
        }
    }

    private void cacheCode(String code, Long memberId) {
        if (code == null || memberId == null) return;
        long expiresAt = Instant.now().toEpochMilli() + CODE_CACHE_TTL_MILLIS;
        codeCache.put(code, new CodeCacheEntry(memberId, expiresAt));

        // 단건 캐시 저장 로그
        log.info("[cacheCode] cached code={} → memberId={}, expiresAt={}",
                mask(code), memberId, Instant.ofEpochMilli(expiresAt));

        // 전체 캐시 스냅샷 출력
        logCacheSnapshot();
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

    private boolean isInvalidGrant(HttpClientErrorException.BadRequest e) {
        String body = e.getResponseBodyAsString();
        return body.contains("invalid_grant");
    }

    /**
     * 게스트 토큰 발급 헬퍼.
     * JwtTokenService API에 맞게 메서드명을 조정하세요.
     */
    private TokenResponse issueGuestToken(Long memberId) {
        return jwtTokenService.generateGuestToken(memberId);
    }

    /**
     * codeCache 전체 내용을 보기 좋게 출력합니다.
     * (code는 일부만 마스킹하여 노출)
     */
    private void logCacheSnapshot() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("[codeCache] size=").append(codeCache.size());
            long now = Instant.now().toEpochMilli();

            for (Map.Entry<String, CodeCacheEntry> entry : codeCache.entrySet()) {
                CodeCacheEntry c = entry.getValue();
                long ttlMs = c.expiresAtMs - now;
                sb.append(System.lineSeparator())
                        .append(" - code=").append(mask(entry.getKey()))
                        .append(", memberId=").append(c.memberId)
                        .append(", expiresAt=").append(Instant.ofEpochMilli(c.expiresAtMs))
                        .append(" (in ").append(ttlMs).append("ms)");
            }
            log.info(sb.toString());
        } catch (Exception e) {
            log.warn("Failed to log codeCache snapshot", e);
        }
    }

    /**
     * 민감한 code 문자열을 일부만 보이도록 마스킹
     */
    private static String mask(String s) {
        if (s == null) return "null";
        int n = s.length();
        if (n <= 7) return "***";
        return s.substring(0, 4) + "..." + s.substring(n - 3);
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
