package com.kuit.kupage.domain.memberRole.service;

import com.kuit.kupage.common.auth.AuthTokenResponse;
import com.kuit.kupage.common.auth.JwtTokenService;
import com.kuit.kupage.common.constant.ConstantProperties;
import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.member.repository.MemberRepository;
import com.kuit.kupage.domain.memberRole.MemberRole;
import com.kuit.kupage.domain.memberRole.repository.MemberRoleRepository;
import com.kuit.kupage.domain.oauth.dto.DiscordInfoResponse;
import com.kuit.kupage.domain.oauth.dto.DiscordTokenResponse;
import com.kuit.kupage.domain.oauth.dto.LoginOrSignupResult;
import com.kuit.kupage.domain.role.Role;
import com.kuit.kupage.exception.KupageException;
import com.kuit.kupage.exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.kuit.kupage.common.auth.AuthRole.GUEST;
import static com.kuit.kupage.common.response.ResponseCode.MEMBER_SIGNUP_CONFLICT;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberRoleService {

    private final JwtTokenService jwtTokenService;
    private final MemberRepository memberRepository;
    private final ConstantProperties constantProperties;
    private final MemberRoleRepository memberRoleRepository;

    public List<MemberRole> getMemberRolesByMemberId(Long memberId) {
        return memberRoleRepository.findWithRoleByMemberId(memberId);
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
    public LoginOrSignupResult processLoginOrSignup(DiscordTokenResponse response, DiscordInfoResponse userInfo) {
        Long memberId = getMemberIdByDiscordInfo(userInfo);
        log.info("[processLoginOrSignup] memberId = {}", memberId);

        if (memberId != null) {
            log.info("[processLoginOrSignup] 기존 회원 로그인 처리");
            List<String> roleNames = getMemberCurrentRolesByMemberId(memberId).stream()
                    .map(Role::getName)
                    .toList();
            return new LoginOrSignupResult(memberId, roleNames, updateToken(memberId, response));
        }

        try {
            log.info("[processLoginOrSignup] 신규 회원 회원가입 처리");
            return signup(response, userInfo);
        } catch (DataIntegrityViolationException e) {
            log.warn("[processLoginOrSignup] signup 중 unique 제약조건 위반 발생. " +
                            "동시 가입 요청(race condition) 가능성. discordId={}",
                    userInfo.getUserResponse().getId(), e);

            // 이미 다른 트랜잭션에서 가입이 끝났을 수 있으니 다시 조회
            Long existingMemberId = getMemberIdByDiscordInfo(userInfo);
            if (existingMemberId != null) {
                log.info("[processLoginOrSignup] unique 예외 이후 재조회 결과, 기존 회원으로 판단 → 로그인 처리");
                List<String> roleNames = getMemberCurrentRolesByMemberId(existingMemberId).stream()
                        .map(Role::getName)
                        .toList();

                return new LoginOrSignupResult(
                        existingMemberId,
                        roleNames,
                        updateToken(existingMemberId, response)
                );
            }
            throw new KupageException(MEMBER_SIGNUP_CONFLICT);
        }
    }

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

    public boolean isCurrentBatch(Long memberId) {
        return memberRoleRepository.existsByMember_IdAndRole_Batch(memberId, constantProperties.getCurrentBatch());
    }

    @Transactional
    public void updateMemberRoles(Member member) {
        List<MemberRole> memberRoles = memberRoleRepository.findByMemberDiscordId(member.getDiscordId());
        memberRoles.forEach(member::addMemberRole);
        log.info("[updateMemberRoles] member = {}의 역할 {}개 업데이트", member.getDiscordLoginId(), memberRoles.size());
    }

    private Long getMemberIdByDiscordInfo(DiscordInfoResponse userInfo) {
        String discordId = userInfo.getUserResponse().getId();
        return memberRepository.findByDiscordId(discordId)
                .map(Member::getId)
                .orElse(null);
    }

    public List<Role> getMemberCurrentRolesByMemberId(Long memberId) {
        return getMemberRolesByMemberId(memberId).stream()
                .map(MemberRole::getRole)
                .filter(role -> role.getBatch() == constantProperties.getCurrentBatch())
                .toList();
    }

    //todo 리프레시 토큰을 db에 저장할지 레디스에 저장할지?
//    private AuthTokenResponse issueAndUpdateAuthToken(Member member) {
//        AuthTokenResponse authTokenResponse = jwtTokenService.generateTokens(member.getId());
//        log.debug("[issueAndUpdateAuthToken] 발급받은 auth token = {}", authTokenResponse);
//        member.updateAuthToken(authTokenResponse);
//        return authTokenResponse;
//    }

}