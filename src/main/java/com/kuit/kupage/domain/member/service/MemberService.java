package com.kuit.kupage.domain.member.service;

import com.kuit.kupage.common.oauth.dto.discordInfo.DiscordInfoResponse;
import com.kuit.kupage.common.oauth.dto.DiscordTokenResponse;
import com.kuit.kupage.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public void updateOauthToken(DiscordTokenResponse response) {
        // TODO. 디코에서 응답으로 보내준 access token, 만료 시간을 redis 또는 db에 저장
    }

    public Long lookupMemberId(DiscordInfoResponse userInfo) {
        // TODO. 해당 사용자가 기존 회원인지 신규 회원인지를 user.id를 통해 확인
        return null;
    }

    public Long signup(DiscordTokenResponse response, DiscordInfoResponse userInfo) {
        // TODO. 신규회원 회원가입 처리
        return null;
    }
}
