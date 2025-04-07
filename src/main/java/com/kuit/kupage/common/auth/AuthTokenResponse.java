package com.kuit.kupage.common.auth;

import com.kuit.kupage.common.response.BaseResponse;

import static com.kuit.kupage.common.response.ResponseStatus.SUCCESS;

public record AuthTokenResponse(String accessToken, String refreshToken) implements TokenResponse {
    @Override
    public BaseResponse<? extends TokenResponse> toBaseResponse() {
        return new BaseResponse<>(true, SUCCESS, "기존 회원. 로그인 처리", this);
    }
}

