package com.kuit.kupage.common.auth;

import com.kuit.kupage.common.response.BaseResponse;

public interface TokenResponse {
     default BaseResponse<? extends TokenResponse> toBaseResponse(){
        throw new UnsupportedOperationException("하위 클래스에서 구현해야 합니다.");      // TODO. merge 후 KupageException으로 변경해야함
    }
}
