package com.kuit.kupage.domain.oauth.service;

import com.kuit.kupage.common.auth.JwtTokenService;
import com.kuit.kupage.domain.memberRole.service.MemberRoleService;
import com.kuit.kupage.domain.oauth.DiscordApiType;
import com.kuit.kupage.exception.KupageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import static com.kuit.kupage.common.response.ResponseCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class DiscordOAuthServiceTest {

    private DiscordOAuthService service;
    private Method callDiscordMethod;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        // RestClient.Builder / MemberRoleService / JwtTokenService 는 실제로 사용되지 않으므로 mock 으로 충분
        RestClient.Builder builder = RestClient.builder();
        MemberRoleService memberRoleService = mock(MemberRoleService.class);
        JwtTokenService jwtTokenService = mock(JwtTokenService.class);

        service = new DiscordOAuthService(builder, memberRoleService, jwtTokenService);

        // private <T> T callDiscord(Supplier<T> caller, DiscordApiType type) 리플렉션으로 접근
        callDiscordMethod = DiscordOAuthService.class
                .getDeclaredMethod("callDiscord", Supplier.class, DiscordApiType.class);
        callDiscordMethod.setAccessible(true);
    }

    /**
     * 리플렉션으로 callDiscord 를 호출하면서, Supplier 안에서 원하는 예외를 던지기 위한 헬퍼.
     * 내부에서 InvocationTargetException 을 벗겨서 실제 cause 를 다시 던져줌.
     */
    @SuppressWarnings("unchecked")
    private <T> T invokeCallDiscord(RuntimeException toThrow, DiscordApiType type) {
        Supplier<T> supplier = () -> {
            throw toThrow;
        };

        try {
            return (T) callDiscordMethod.invoke(service, supplier, type);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private HttpClientErrorException http4xx(HttpStatus status, String body) {
        byte[] bytes = body == null ? null : body.getBytes(StandardCharsets.UTF_8);
        return new HttpClientErrorException(status, status.getReasonPhrase(), bytes, StandardCharsets.UTF_8);
    }

    // ===== TOKEN (access token 교환) 관련 테스트 =====

    @Test
    @DisplayName("TOKEN - invalid_grant + Invalid \"code\" → DISCORD_OAUTH_INVALID_GRANT")
    void token_invalidCode() {
        String body = "{\"error\":\"invalid_grant\",\"error_description\":\"Invalid \\\"code\\\" in request.\"}";
        KupageException ex = assertThrows(
                KupageException.class,
                () -> invokeCallDiscord(http4xx(HttpStatus.BAD_REQUEST, body), DiscordApiType.TOKEN)
        );
        assertEquals(DISCORD_OAUTH_INVALID_GRANT, ex.getResponseCode());
    }

    @Test
    @DisplayName("TOKEN - invalid_grant(코드 문제 외) → DISCORD_OAUTH_INVALID_GRANT")
    void token_invalidGrant() {
        String body = "{\"error\":\"invalid_grant\",\"error_description\":\"something else\"}";
        KupageException ex = assertThrows(
                KupageException.class,
                () -> invokeCallDiscord(http4xx(HttpStatus.BAD_REQUEST, body), DiscordApiType.TOKEN)
        );
        assertEquals(DISCORD_OAUTH_INVALID_GRANT, ex.getResponseCode());
    }

    @Test
    @DisplayName("TOKEN - invalid_client → DISCORD_OAUTH_INVALID_CLIENT")
    void token_invalidClient() {
        String body = "{\"error\":\"invalid_client\"}";
        KupageException ex = assertThrows(
                KupageException.class,
                () -> invokeCallDiscord(http4xx(HttpStatus.BAD_REQUEST, body), DiscordApiType.TOKEN)
        );
        assertEquals(DISCORD_OAUTH_INVALID_CLIENT, ex.getResponseCode());
    }

    @Test
    @DisplayName("TOKEN - 400 기타 → DISCORD_OAUTH_BAD_REQUEST")
    void token_badRequest_generic() {
        String body = "{\"error\":\"something_else\"}";
        KupageException ex = assertThrows(
                KupageException.class,
                () -> invokeCallDiscord(http4xx(HttpStatus.BAD_REQUEST, body), DiscordApiType.TOKEN)
        );
        assertEquals(DISCORD_OAUTH_BAD_REQUEST, ex.getResponseCode());
    }

    @Test
    @DisplayName("TOKEN - 401 → DISCORD_OAUTH_UNAUTHORIZED")
    void token_unauthorized() {
        KupageException ex = assertThrows(
                KupageException.class,
                () -> invokeCallDiscord(http4xx(HttpStatus.UNAUTHORIZED, "{}"), DiscordApiType.TOKEN)
        );
        assertEquals(DISCORD_OAUTH_UNAUTHORIZED, ex.getResponseCode());
    }

    @Test
    @DisplayName("TOKEN - 403 → DISCORD_OAUTH_FORBIDDEN")
    void token_forbidden() {
        KupageException ex = assertThrows(
                KupageException.class,
                () -> invokeCallDiscord(http4xx(HttpStatus.FORBIDDEN, "{}"), DiscordApiType.TOKEN)
        );
        assertEquals(DISCORD_OAUTH_FORBIDDEN, ex.getResponseCode());
    }

    @Test
    @DisplayName("TOKEN - 429 → DISCORD_OAUTH_RATE_LIMITED")
    void token_rateLimited() {
        KupageException ex = assertThrows(
                KupageException.class,
                () -> invokeCallDiscord(http4xx(HttpStatus.TOO_MANY_REQUESTS, "{}"), DiscordApiType.TOKEN)
        );
        assertEquals(DISCORD_OAUTH_RATE_LIMITED, ex.getResponseCode());
    }

    @Test
    @DisplayName("TOKEN - 기타 4xx → DISCORD_OAUTH_CLIENT_ERROR")
    void token_other4xx() {
        KupageException ex = assertThrows(
                KupageException.class,
                () -> invokeCallDiscord(http4xx(HttpStatus.I_AM_A_TEAPOT, "{}"), DiscordApiType.TOKEN)
        );
        assertEquals(DISCORD_OAUTH_CLIENT_ERROR, ex.getResponseCode());
    }

    @Test
    @DisplayName("TOKEN - HttpClientErrorException 이외 예외 → DISCORD_OAUTH_SERVER_ERROR")
    void token_unknownException() {
        KupageException ex = assertThrows(
                KupageException.class,
                () -> invokeCallDiscord(new RuntimeException("boom"), DiscordApiType.TOKEN)
        );
        assertEquals(DISCORD_OAUTH_SERVER_ERROR, ex.getResponseCode());
    }

    // ===== USER_INFO (유저 정보 조회) 관련 테스트 =====

    @Test
    @DisplayName("USER_INFO - 400 → DISCORD_OAUTH_BAD_REQUEST")
    void userInfo_badRequest() {
        KupageException ex = assertThrows(
                KupageException.class,
                () -> invokeCallDiscord(http4xx(HttpStatus.BAD_REQUEST, "{}"), DiscordApiType.USER_INFO)
        );
        assertEquals(DISCORD_OAUTH_BAD_REQUEST, ex.getResponseCode());
    }

    @Test
    @DisplayName("USER_INFO - 401 → DISCORD_OAUTH_UNAUTHORIZED")
    void userInfo_unauthorized() {
        KupageException ex = assertThrows(
                KupageException.class,
                () -> invokeCallDiscord(http4xx(HttpStatus.UNAUTHORIZED, "{}"), DiscordApiType.USER_INFO)
        );
        assertEquals(DISCORD_OAUTH_UNAUTHORIZED, ex.getResponseCode());
    }

    @Test
    @DisplayName("USER_INFO - 403 → DISCORD_OAUTH_FORBIDDEN")
    void userInfo_forbidden() {
        KupageException ex = assertThrows(
                KupageException.class,
                () -> invokeCallDiscord(http4xx(HttpStatus.FORBIDDEN, "{}"), DiscordApiType.USER_INFO)
        );
        assertEquals(DISCORD_OAUTH_FORBIDDEN, ex.getResponseCode());
    }

    @Test
    @DisplayName("USER_INFO - 429 → DISCORD_OAUTH_RATE_LIMITED")
    void userInfo_rateLimited() {
        KupageException ex = assertThrows(
                KupageException.class,
                () -> invokeCallDiscord(http4xx(HttpStatus.TOO_MANY_REQUESTS, "{}"), DiscordApiType.USER_INFO)
        );
        assertEquals(DISCORD_OAUTH_RATE_LIMITED, ex.getResponseCode());
    }

    @Test
    @DisplayName("USER_INFO - 기타 4xx → DISCORD_OAUTH_CLIENT_ERROR")
    void userInfo_other4xx() {
        KupageException ex = assertThrows(
                KupageException.class,
                () -> invokeCallDiscord(http4xx(HttpStatus.I_AM_A_TEAPOT, "{}"), DiscordApiType.USER_INFO)
        );
        assertEquals(DISCORD_OAUTH_CLIENT_ERROR, ex.getResponseCode());
    }

    @Test
    @DisplayName("USER_INFO - HttpClientErrorException 이외 예외 → DISCORD_OAUTH_SERVER_ERROR")
    void userInfo_unknownException() {
        KupageException ex = assertThrows(
                KupageException.class,
                () -> invokeCallDiscord(new RuntimeException("boom"), DiscordApiType.USER_INFO)
        );
        assertEquals(DISCORD_OAUTH_SERVER_ERROR, ex.getResponseCode());
    }

    // ===== GUILD_ROLES (길드 역할 조회) 관련 테스트 =====

    @Test
    @DisplayName("GUILD_ROLES - 401 → DISCORD_BOT_INVALID_TOKEN")
    void guildRoles_invalidToken() {
        KupageException ex = assertThrows(
                KupageException.class,
                () -> invokeCallDiscord(http4xx(HttpStatus.UNAUTHORIZED, "{}"), DiscordApiType.GUILD_ROLES)
        );
        assertEquals(DISCORD_BOT_INVALID_TOKEN, ex.getResponseCode());
    }

    @Test
    @DisplayName("GUILD_ROLES - 403 → DISCORD_BOT_FORBIDDEN")
    void guildRoles_forbidden() {
        KupageException ex = assertThrows(
                KupageException.class,
                () -> invokeCallDiscord(http4xx(HttpStatus.FORBIDDEN, "{}"), DiscordApiType.GUILD_ROLES)
        );
        assertEquals(DISCORD_BOT_FORBIDDEN, ex.getResponseCode());
    }

    @Test
    @DisplayName("GUILD_ROLES - 기타 4xx → DISCORD_ROLE_FETCH_FAIL")
    void guildRoles_other4xx() {
        KupageException ex = assertThrows(
                KupageException.class,
                () -> invokeCallDiscord(http4xx(HttpStatus.BAD_REQUEST, "{}"), DiscordApiType.GUILD_ROLES)
        );
        assertEquals(DISCORD_ROLE_FETCH_FAIL, ex.getResponseCode());
    }

    @Test
    @DisplayName("GUILD_ROLES - HttpClientErrorException 이외 예외 → DISCORD_ROLE_FETCH_FAIL")
    void guildRoles_unknownException() {
        KupageException ex = assertThrows(
                KupageException.class,
                () -> invokeCallDiscord(new RuntimeException("boom"), DiscordApiType.GUILD_ROLES)
        );
        assertEquals(DISCORD_ROLE_FETCH_FAIL, ex.getResponseCode());
    }

    // ===== GUILD_MEMBERS (길드 멤버 조회) 관련 테스트 =====

    @Test
    @DisplayName("GUILD_MEMBERS - 401 → DISCORD_BOT_INVALID_TOKEN")
    void guildMembers_invalidToken() {
        KupageException ex = assertThrows(
                KupageException.class,
                () -> invokeCallDiscord(http4xx(HttpStatus.UNAUTHORIZED, "{}"), DiscordApiType.GUILD_MEMBERS)
        );
        assertEquals(DISCORD_BOT_INVALID_TOKEN, ex.getResponseCode());
    }

    @Test
    @DisplayName("GUILD_MEMBERS - 403 → DISCORD_BOT_FORBIDDEN")
    void guildMembers_forbidden() {
        KupageException ex = assertThrows(
                KupageException.class,
                () -> invokeCallDiscord(http4xx(HttpStatus.FORBIDDEN, "{}"), DiscordApiType.GUILD_MEMBERS)
        );
        assertEquals(DISCORD_BOT_FORBIDDEN, ex.getResponseCode());
    }

    @Test
    @DisplayName("GUILD_MEMBERS - 기타 4xx → DISCORD_MEMBER_FETCH_FAIL")
    void guildMembers_other4xx() {
        KupageException ex = assertThrows(
                KupageException.class,
                () -> invokeCallDiscord(http4xx(HttpStatus.BAD_REQUEST, "{}"), DiscordApiType.GUILD_MEMBERS)
        );
        assertEquals(DISCORD_MEMBER_FETCH_FAIL, ex.getResponseCode());
    }

    @Test
    @DisplayName("GUILD_MEMBERS - HttpClientErrorException 이외 예외 → DISCORD_MEMBER_FETCH_FAIL")
    void guildMembers_unknownException() {
        KupageException ex = assertThrows(
                KupageException.class,
                () -> invokeCallDiscord(new RuntimeException("boom"), DiscordApiType.GUILD_MEMBERS)
        );
        assertEquals(DISCORD_MEMBER_FETCH_FAIL, ex.getResponseCode());
    }
}