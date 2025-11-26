package com.kuit.kupage.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum ResponseCode {

    // 1000 번대 : global 요청 성공/실패
    SUCCESS(true, 1000, "요청에 성공하였습니다."),
    ROLE_REQUIRE(false, 1001, "역할이 지정되지 않았습니다. 운영진에게 문의주세요."),
    AWS_S3_UPLOAD_ISSUE(false, 1002, "파일 업로드 중 문제가 발생했습니다."),
    EXPIRED_ACCESS_TOKEN(false, 1003, "이미 만료된 Access 토큰입니다."),
    UNSUPPORTED_TOKEN_TYPE(false, 1004, "지원되지 않는 토큰 형식입니다."),
    MALFORMED_TOKEN_TYPE(false, 1005, "인증 토큰이 올바르게 구성되지 않았습니다."),
    INVALID_SIGNATURE_JWT(false, 1006, "인증 시그니처가 올바르지 않습니다"),
    INVALID_TOKEN_TYPE(false, 1007, "잘못된 토큰입니다."),
    BAD_REQUEST(false, 1008, "잘못된 요청입니다."),

    // DB 오류
    SQL_EXCEPTION(false, 1009, "데이터베이스 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    DATA_INTEGRITY_VIOLATION(false, 1010, "데이터 무결성 제약조건을 위반했습니다."),
    INCORRECT_RESULT_SIZE(false, 1011, "요청한 결과 개수가 올바르지 않습니다."),
    OPTIMISTIC_LOCK_FAILURE(false, 1012, "다른 요청에 의해 데이터가 변경되었습니다. 다시 시도해 주세요."),
    QUERY_TIMEOUT(false, 1013, "데이터베이스 요청 시간이 초과되었습니다."),
    PESSIMISTIC_LOCK_FAILURE(false, 1014, "데이터베이스 잠금 획득 중 오류가 발생했습니다. 다시 시도해 주세요."),

    // 2000 번대 : auth 관련 상태 코드
    GUEST_REQUIRED_SIGNUP(true, 2000, "회원가입 처리가 완료되지 않은 회원입니다. 회원가입을 시도하세요."),
    NONE_MEMBER(false, 2001, "존재하지 않는 회원입니다."),
    ALREADY_MEMBER(false, 2002, "이미 회원가입 된 멤버입니다."),
    NOT_CURRENT_BATCH_MEMBER(false, 2003, "이전 기수는 사용할 수 없는 기능입니다. 만약 현재 기수라면 다시 로그인을 해주십시오."),
    FORBIDDEN(false, 2004, "요청 권한이 없습니다."),
    MEMBER_SIGNUP_CONFLICT(false, 2005, "회원 가입 중 충돌이 발생했습니다. 다시 시도해주세요."),


    // 3000 번대 : discord 외부 API 관련 코드
    DISCORD_ROLE_FETCH_FAIL(false, 3000, "디스코드 역할 조회 실패"),
    DISCORD_MEMBER_FETCH_FAIL(false, 3001, "디스코드 멤버 조회 실패"),
    DISCORD_BOT_INVALID_TOKEN(false, 3002, "Discord Bot 토큰이 유효하지 않습니다."),
    DISCORD_BOT_FORBIDDEN(false, 3003, "Discord Bot이 역할 조회 권한이 없습니다."),
    DISCORD_ROLE_CONVERT_FAIL(false, 3005, "역할 이름을 Role로 변경하는 중 오류 발생"),

    DISCORD_OAUTH_INVALID_CODE(false, 3006, "유효하지 않거나 만료된 디스코드 인가 코드입니다."),
    DISCORD_OAUTH_INVALID_GRANT(false, 3007, "디스코드 OAuth grant가 유효하지 않습니다."),
    DISCORD_OAUTH_INVALID_CLIENT(false, 3008, "디스코드 OAuth 클라이언트 설정이 올바르지 않습니다."),
    DISCORD_OAUTH_BAD_REQUEST(false, 3009, "디스코드 OAuth 요청 형식이 올바르지 않습니다."),
    DISCORD_OAUTH_UNAUTHORIZED(false, 3010, "디스코드 OAuth 클라이언트 인증에 실패했습니다."),
    DISCORD_OAUTH_FORBIDDEN(false, 3011, "디스코드 OAuth 권한이 없습니다."),
    DISCORD_OAUTH_RATE_LIMITED(false, 3012, "디스코드 OAuth 요청 제한을 초과했습니다."),
    DISCORD_OAUTH_CLIENT_ERROR(false, 3013, "디스코드 OAuth 클라이언트 오류가 발생했습니다."),
    DISCORD_OAUTH_SERVER_ERROR(false, 3014, "디스코드 OAuth 서버 오류가 발생했습니다."),

    // 4000 번대 : article 관련 코드
    INVALID_TITLE(false, 4000, "아티클의 제목의 형식을 확인해주세요."),
    INVALID_TAGS(false, 4001, "올바른 태그 목록을 입력하세요."),
    INVALID_POSITIONS(false, 4002, "블럭의 position 형식을 다시 확인해주세요"),

    INVALID_IMAGE_TYPE(false, 4003, "지원하지 않는 이미지 형식입니다."),
    TOO_BIG_IMAGE(false, 4004, "이미지의 크기가 너무 큽니다."),
    TOO_MANY_IMAGE(false, 4005, "이미지 블럭의 개수가 너무 많습니다."),

    TOO_BIG_FILE(false, 4006, "파일의 크기가 너무 큽니다."),
    TOO_MANY_FILE(false, 4007, "파일 블럭의 개수가 너무 많습니다."),

    PARSING_ISSUE(false, 4008, "블럭의 properties 형식을 확인해주세요."),

    INVALID_TITLE_PROPERTY(false, 4009, "블럭의 title property를 다시 확인해주세요."),
    INVALID_URL_PROPERTY(false, 4010, "블럭의 url property를 다시 확인해주세요."),
    INVALID_CODE_LANG_PROPERTY(false, 4011, "블럭의 code_lang property를 다시 확인해주세요"),

    NONE_ARTICLE(false, 4012, "존재하지 않는 아티클입니다."),


    // 5000 : project 관련
    NONE_PROJECT(false, 5000, "존재하지 않는 프로젝트입니다."),


    // 6000 번대 : team-match 관련
    NONE_TEAM(false, 6000, "존재하지 않는 팀입니다."),
    NONE_OWN_TEAM(false, 6001, "소유하는 팀이 존재하지 않습니다."),
    NONE_APPLIED_TEAM(false, 6002, "지원한 팀이 존재하지 않습니다."),
    REJECTED_TEAM_MATCH(false, 6003, "매칭되지 않았습니다."),
    NOT_TEAM_MATCH_APPLY_PERIOD(false, 6004, "팀매칭 지원 기간이 아닙니다."),
    DUPLICATED_TEAM_APPLY(false, 6005, "동일한 팀에 여러번 지원할 수 없습니다."),
    EXCEEDED_TEAM_APPLY_LIMIT(false, 6006, "한 멤버 당 최대 2개의 팀에만 지원할 수 있습니다."),
    TEAM_APPLY_FAILED(false, 6007, "팀매칭 지원에 실패했습니다. 다시 시도해주세요.");


    private boolean isSuccess;
    private int code;
    private String message;
}
