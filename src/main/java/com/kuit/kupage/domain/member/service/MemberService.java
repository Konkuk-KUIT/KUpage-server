package com.kuit.kupage.domain.member.service;

import com.kuit.kupage.common.auth.AuthTokenResponse;
import com.kuit.kupage.common.jwt.JwtTokenService;
import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.domain.detail.Detail;
import com.kuit.kupage.domain.detail.dto.MyPageResponse;
import com.kuit.kupage.domain.detail.dto.MyPageUpdateRequest;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.member.repository.MemberRepository;
import com.kuit.kupage.domain.oauth.dto.DiscordInfoResponse;
import com.kuit.kupage.domain.oauth.dto.DiscordTokenResponse;
import com.kuit.kupage.domain.oauth.dto.LoginOrSignupResult;
import com.kuit.kupage.exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.kuit.kupage.common.auth.AuthRole.GUEST;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtTokenService jwtTokenService;

    @Transactional
    public LoginOrSignupResult signup(DiscordTokenResponse response, DiscordInfoResponse userInfo) {
        log.debug("[signup] 신규 회원 회원가입 처리 : 추가 정보 받기 -> 회원가입 처리 -> AuthToken 발급");
        Member member = new Member(response, userInfo);
        Member savedMember = memberRepository.save(member);
        log.debug("[signup] 신규 회원 member = {}", savedMember);
        return new LoginOrSignupResult(savedMember.getId(), List.of(GUEST.getValue()), jwtTokenService.generateGuestToken(savedMember.getId()));
    }


    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ResponseCode.NONE_MEMBER));
    }

    @Transactional
    public AuthTokenResponse updateToken(Long memberId, DiscordTokenResponse response) {
        log.info("[updateToken] 기존 회원 로그인 처리 : AuthToken 발급");
        Member member =
                getMember(memberId);
        member.updateOauthToken(response);
        return jwtTokenService.generateTokens(member);
//        return issueAndUpdateAuthToken(member);
    }

    private Member getMemberWithDetail(Long memberId) {
        Member member = memberRepository.findByIdWithDetail(memberId)
                .orElseThrow(() -> new MemberException(ResponseCode.NONE_MEMBER));
        if (member.getDetail() == null) {
            throw new MemberException(ResponseCode.NONE_DETAIL);
        }
        return member;
    }


    public MyPageResponse getMyInfo(Long memberId, List<String> roles) {
        Member member = getMemberWithDetail(memberId);
        return MyPageResponse.from(member, roles);
    }

    @Transactional
    public void updateMyInfo(MyPageUpdateRequest request, Long memberId) {
        Member member = getMemberWithDetail(memberId);
        Detail detail = Detail.of(request.name(),
                request.studentNumber(),
                request.departName(),
                request.grade(),
                request.githubId(),
                request.email(),
                request.phoneNumber(),
                request.birthday()
        );
        member.updateDetail(request.name(), detail);
    }


    //todo 리프레시 토큰을 db에 저장할지 레디스에 저장할지?
//    private AuthTokenResponse issueAndUpdateAuthToken(Member member) {
//        AuthTokenResponse authTokenResponse = jwtTokenService.generateTokens(member.getId());
//        log.debug("[issueAndUpdateAuthToken] 발급받은 auth token = {}", authTokenResponse);
//        member.updateAuthToken(authTokenResponse);
//        return authTokenResponse;
//    }
}