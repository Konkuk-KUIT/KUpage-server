package com.kuit.kupage.domain.member.service;

import com.kuit.kupage.common.oauth.dto.DiscordTokenResponse;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    public void updateOauthToken(DiscordTokenResponse response) {
        // TODO. 디코에서 응답으로 보내준 access token, 만료 시간을 redis 또는 db에 저장
    }
}
