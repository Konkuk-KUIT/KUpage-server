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

    // 2000 번대 : auth 관련 상태 코드
    GUEST_REQUIRED_SIGNUP(true, 2000, "회원가입 처리가 완료되지 않은 회원입니다. 회원가입을 시도하세요."),
    NONE_MEMBER(false, 2001, "존재하지 않는 회원입니다."),
    ALREADY_MEMBER(false, 2002, "이미 회원가입 된 멤버입니다.");

    private boolean isSuccess;
    private int code;
    private String message;
}
