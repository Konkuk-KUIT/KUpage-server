package com.kuit.kupage.domain.member.service;

import com.kuit.kupage.common.auth.AuthTokenResponse;
import com.kuit.kupage.common.auth.JwtTokenService;
import com.kuit.kupage.common.oauth.dto.DiscordInfoResponse;
import com.kuit.kupage.common.oauth.dto.DiscordTokenResponse;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {

    private final JwtTokenService jwtTokenService;
    private final MemberRepository memberRepository;

    public Long lookupMemberId(DiscordInfoResponse userInfo) {
        String discordId = userInfo.getUserResponse().getId();
        return memberRepository.findByDiscordId(discordId)
                .map(Member::getId)
                .orElse(null);
    }

    public AuthTokenResponse updateToken(Long memberId, DiscordTokenResponse response) {
        // 기존 회원 : AuthToken 발급&홈으로 리다이렉트 (로그인 처리)
        Member member = getMember(memberId);
        AuthTokenResponse authTokenResponse = jwtTokenService.generateTokens(member.getId());
        member.updateOauthToken(response.getAccessToken(), response.getRefreshToken(), response.getExpiresIn());
        member.updateAuthToken(authTokenResponse.getAccessToken(), authTokenResponse.getRefreshToken());
        return authTokenResponse;
    }

    public AuthTokenResponse signup(DiscordTokenResponse response, DiscordInfoResponse userInfo) {
        // TODO. 신규회원 회원가입 처리
        return null;
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(IllegalArgumentException::new);
    }
}
