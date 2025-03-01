package com.kuit.kupage.domain.member.service;

import com.kuit.kupage.common.auth.AuthTokenResponse;
import com.kuit.kupage.common.auth.JwtTokenService;
import com.kuit.kupage.common.oauth.dto.DiscordInfoResponse;
import com.kuit.kupage.common.oauth.dto.DiscordTokenResponse;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
        log.info("[updateToken] 기존 회원 로그인 처리 : AuthToken 발급");
        Member member = getMember(memberId);
        member.updateOauthToken(response);
        return issueAndUpdateAuthToken(member);
    }

    public AuthTokenResponse signup(DiscordTokenResponse response, DiscordInfoResponse userInfo) {
        log.info("[signup] 신규 회원 회원가입 처리 : 추가 정보 받기 -> 회원가입 처리 -> AuthToken 발급");
        Member member = new Member(response, userInfo);
        Member savedMember = memberRepository.save(member);
        log.info("[signup] 신규 회원 member = {}", savedMember);
        return issueAndUpdateAuthToken(savedMember);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(IllegalArgumentException::new);
    }

    private AuthTokenResponse issueAndUpdateAuthToken(Member member) {
        AuthTokenResponse authTokenResponse = jwtTokenService.generateTokens(member.getId());
        log.info("[issueAndUpdateAuthToken] 발급받은 auth token = {}", authTokenResponse);
        member.updateAuthToken(authTokenResponse);
        return authTokenResponse;
    }
}
