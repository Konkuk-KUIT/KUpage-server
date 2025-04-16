package com.kuit.kupage.common.auth;

import com.kuit.kupage.common.response.BaseResponse;

import static com.kuit.kupage.common.response.ResponseCode.SUCCESS;

public record AuthTokenResponse(String accessToken, String refreshToken) implements TokenResponse {
    @Override
    public BaseResponse<? extends TokenResponse> toBaseResponse() {
        return new BaseResponse<>(SUCCESS, this);
    }
}

