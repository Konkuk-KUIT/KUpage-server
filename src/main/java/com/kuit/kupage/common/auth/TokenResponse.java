package com.kuit.kupage.common.auth;

import com.kuit.kupage.common.response.BaseResponse;

public interface TokenResponse {
    BaseResponse<? extends TokenResponse> toBaseResponse();
}
