package com.kuit.kupage.common.auth;

import com.kuit.kupage.common.response.BaseResponse;

import static com.kuit.kupage.common.response.ResponseStatus.*;

public record GuestTokenResponse(String guestToken) implements TokenResponse {
    @Override
    public BaseResponse<? extends TokenResponse> toBaseResponse() {
        return new BaseResponse<>(false, GUEST_REQUIRED_SIGNUP, "신규 회원. 회원가입 처리", this);
    }
}
