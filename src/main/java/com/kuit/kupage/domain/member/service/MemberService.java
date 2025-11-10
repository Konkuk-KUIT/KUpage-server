package com.kuit.kupage.domain.member.service;

import com.kuit.kupage.common.auth.AuthTokenResponse;
import com.kuit.kupage.common.auth.JwtTokenService;
import com.kuit.kupage.common.auth.TokenResponse;
import com.kuit.kupage.common.constant.ConstantProperties;
import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.member.repository.MemberRepository;
import com.kuit.kupage.domain.memberRole.repository.MemberRoleRepository;
import com.kuit.kupage.domain.oauth.dto.DiscordInfoResponse;
import com.kuit.kupage.domain.oauth.dto.DiscordTokenResponse;
import com.kuit.kupage.exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final JwtTokenService jwtTokenService;
    private final MemberRepository memberRepository;
    private final MemberRoleRepository memberRoleRepository;
    private final ConstantProperties constantProperties;

    public Long getMemberIdByDiscordInfo(DiscordInfoResponse userInfo) {
        String discordId = userInfo.getUserResponse().getId();
        return memberRepository.findByDiscordId(discordId)
                .map(Member::getId)
                .orElse(null);
    }

    @Transactional
    public AuthTokenResponse updateToken(Long memberId, DiscordTokenResponse response) {
        log.info("[updateToken] 기존 회원 로그인 처리 : AuthToken 발급");
        Member member = getMember(memberId);
        member.updateOauthToken(response);
        return jwtTokenService.generateTokens(member);
//        return issueAndUpdateAuthToken(member);
    }

    @Transactional
    public TokenResponse signup(DiscordTokenResponse response, DiscordInfoResponse userInfo) {
        log.debug("[signup] 신규 회원 회원가입 처리 : 추가 정보 받기 -> 회원가입 처리 -> AuthToken 발급");
        Member member = new Member(response, userInfo);
        Member savedMember = memberRepository.save(member);
        log.debug("[signup] 신규 회원 member = {}", savedMember);
        return jwtTokenService.generateGuestToken(savedMember);
    }

    @Transactional(readOnly = true)
    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ResponseCode.NONE_MEMBER));
    }

    public boolean isCurrentBatch(Long memberId) {

        return memberRoleRepository.existsByMember_IdAndRole_Batch(memberId, constantProperties.getCurrentBatch());
    }

    //todo 리프레시 토큰을 db에 저장할지 레디스에 저장할지?
//    private AuthTokenResponse issueAndUpdateAuthToken(Member member) {
//        AuthTokenResponse authTokenResponse = jwtTokenService.generateTokens(member.getId());
//        log.debug("[issueAndUpdateAuthToken] 발급받은 auth token = {}", authTokenResponse);
//        member.updateAuthToken(authTokenResponse);
//        return authTokenResponse;
//    }
}
