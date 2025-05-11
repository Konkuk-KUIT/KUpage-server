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

    // 2000 번대 : auth 관련 상태 코드
    GUEST_REQUIRED_SIGNUP(true, 2000, "회원가입 처리가 완료되지 않은 회원입니다. 회원가입을 시도하세요."),
    NONE_MEMBER(false, 2001, "존재하지 않는 회원입니다."),
    ALREADY_MEMBER(false, 2002, "이미 회원가입 된 멤버입니다."),

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

    NOT_FOUND_ARTICLE(false, 4012, "존재하지 않는 아티클입니다.");

    private boolean isSuccess;
    private int code;
    private String message;
}
