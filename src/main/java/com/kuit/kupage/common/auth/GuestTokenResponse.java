package com.kuit.kupage.common.auth;

import com.kuit.kupage.common.response.BaseResponse;

import static com.kuit.kupage.common.response.ResponseCode.GUEST_REQUIRED_SIGNUP;

public record GuestTokenResponse(String guestToken) implements TokenResponse {
    @Override
    public BaseResponse<? extends TokenResponse> toBaseResponse() {
        return new BaseResponse<>(GUEST_REQUIRED_SIGNUP, this);
    }
}
