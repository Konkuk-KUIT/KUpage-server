package com.kuit.kupage.common.swagger;

import com.kuit.kupage.common.response.ResponseCode;
import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;

import static com.kuit.kupage.common.response.ResponseCode.*;

@Getter
public enum SwaggerErrorResponse {

    /**
     * 인증/토큰 관련 공통 오류 세트
     */
    AUTH_COMMON(new LinkedHashSet<>(Set.of(
            EXPIRED_ACCESS_TOKEN,
            UNSUPPORTED_TOKEN_TYPE,
            MALFORMED_TOKEN_TYPE,
            INVALID_SIGNATURE_JWT,
            INVALID_TOKEN_TYPE,
            FORBIDDEN
    ))),

    DISCORD_OAUTH2(new LinkedHashSet<>(Set.of(
            DISCORD_ROLE_FETCH_FAIL,
            DISCORD_MEMBER_FETCH_FAIL,
            DISCORD_BOT_INVALID_TOKEN,
            DISCORD_BOT_FORBIDDEN,
            DISCORD_ROLE_CONVERT_FAIL,
            DISCORD_OAUTH_INVALID_CODE,
            DISCORD_OAUTH_INVALID_GRANT,
            DISCORD_OAUTH_INVALID_CLIENT,
            DISCORD_OAUTH_BAD_REQUEST,
            DISCORD_OAUTH_UNAUTHORIZED,
            DISCORD_OAUTH_FORBIDDEN,
            DISCORD_OAUTH_RATE_LIMITED,
            DISCORD_OAUTH_CLIENT_ERROR,
            DISCORD_OAUTH_SERVER_ERROR
    ))),

    SIGN_UP(new LinkedHashSet<>(Set.of(
            GUEST_REQUIRED_SIGNUP,
            NONE_MEMBER,
            ALREADY_MEMBER,
            FORBIDDEN
    ))),

    /**
     * 팀매칭 관련 공통 오류 세트
     */
    TEAM_MATCH_VIEW(merge(Set.of(
            NOT_CURRENT_BATCH_MEMBER
    ), AUTH_COMMON)),

    TEAM_MATCH_STATUS(merge(Set.of(
            NOT_CURRENT_BATCH_MEMBER,
            NONE_TEAM,
            NONE_OWN_TEAM,
            NONE_APPLIED_TEAM,
            REJECTED_TEAM_MATCH
    ), AUTH_COMMON)),

    TEAM_MATCH_APPLICANT(merge(Set.of(
            NOT_CURRENT_BATCH_MEMBER,
            NONE_TEAM,
            NONE_OWN_TEAM
    ), AUTH_COMMON)),

    GET_MY_PAGE(merge(Set.of(
            NONE_MEMBER,
            NONE_DETAIL
    ), AUTH_COMMON)),


    UPDATE_MY_PAGE(merge(Set.of(
            INVALID_INPUT_ENUM
    ), AUTH_COMMON)),

    TEAM_MATCH_APPLICANT_DETAIL(merge(Set.of(
            NONE_APPLICANT
    ), AUTH_COMMON)),

    DEFAULT(new LinkedHashSet<>());

    private final Set<ResponseCode> responseCodeSet;

    SwaggerErrorResponse(Set<ResponseCode> responseCodeSet) {
        responseCodeSet.addAll(new LinkedHashSet<>(Set.of(
                BAD_REQUEST
        )));
        this.responseCodeSet = responseCodeSet;
    }

    private static LinkedHashSet<ResponseCode> merge(Set<ResponseCode> seed, SwaggerErrorResponse... refs) {
        LinkedHashSet<ResponseCode> merged = new LinkedHashSet<>(seed);
        for (SwaggerErrorResponse ref : refs) {
            // We are inside the same enum, so direct field access is allowed
            merged.addAll(ref.responseCodeSet);
        }
        return merged;
    }
}
