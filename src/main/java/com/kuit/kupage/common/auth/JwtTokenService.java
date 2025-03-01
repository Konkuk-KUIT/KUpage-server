package com.kuit.kupage.common.auth;

import com.kuit.kupage.common.oauth.dto.DiscordInfoResponse;
import com.kuit.kupage.common.oauth.dto.SignupResponse;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

    public SignupResponse generateTokens(DiscordInfoResponse response) {
        // TODO. 우리 서버용 토큰 발급해서 SignupResponse 생성, 반환
        return null;
    }
}
