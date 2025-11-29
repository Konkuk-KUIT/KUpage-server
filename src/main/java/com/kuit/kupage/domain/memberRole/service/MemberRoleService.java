package com.kuit.kupage.domain.memberRole.service;

import com.kuit.kupage.common.constant.ConstantProperties;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.member.repository.MemberRepository;
import com.kuit.kupage.domain.member.service.MemberService;
import com.kuit.kupage.domain.memberRole.MemberRole;
import com.kuit.kupage.domain.memberRole.repository.MemberRoleRepository;
import com.kuit.kupage.domain.oauth.dto.DiscordInfoResponse;
import com.kuit.kupage.domain.oauth.dto.DiscordTokenResponse;
import com.kuit.kupage.domain.oauth.dto.LoginOrSignupResult;
import com.kuit.kupage.domain.role.Role;
import com.kuit.kupage.exception.KupageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.kuit.kupage.common.response.ResponseCode.MEMBER_SIGNUP_CONFLICT;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberRoleService {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ConstantProperties constantProperties;
    private final MemberRoleRepository memberRoleRepository;

    public List<MemberRole> getMemberRolesByMemberId(Long memberId) {
        return memberRoleRepository.findWithRoleByMemberId(memberId);
    }

    @Transactional
    public LoginOrSignupResult processLoginOrSignup(DiscordTokenResponse response, DiscordInfoResponse userInfo) {
        Member member = getMemberIdByDiscordInfo(userInfo);
        log.info("[processLoginOrSignup] memberId = {}", member.getId());

        if (member.getDetail() != null) {
            log.info("[processLoginOrSignup] 기존 회원 로그인 처리");
            List<String> roleNames = getMemberCurrentRolesByMemberId(member.getId()).stream()
                    .map(Role::getName)
                    .toList();
            return new LoginOrSignupResult(member.getId(), roleNames, memberService.updateToken(member.getId(), response));
        }

        try {
            log.info("[processLoginOrSignup] 신규 회원 회원가입 처리");
            return memberService.signup(response, userInfo);
        } catch (DataIntegrityViolationException e) {
            log.warn("[processLoginOrSignup] signup 중 unique 제약조건 위반 발생. " +
                            "동시 가입 요청(race condition) 가능성. discordId={}",
                    userInfo.getUserResponse().getId(), e);

            // 이미 다른 트랜잭션에서 가입이 끝났을 수 있으니 다시 조회
            Member existingMember = getMemberIdByDiscordInfo(userInfo);
            if (existingMember.getDetail() != null) {
                log.info("[processLoginOrSignup] unique 예외 이후 재조회 결과, 기존 회원으로 판단 → 로그인 처리");
                List<String> roleNames = getMemberCurrentRolesByMemberId(existingMember.getId()).stream()
                        .map(Role::getName)
                        .toList();

                return new LoginOrSignupResult(
                        existingMember.getId(),
                        roleNames,
                        memberService.updateToken(existingMember.getId(), response)
                );
            }
            throw new KupageException(MEMBER_SIGNUP_CONFLICT);
        }
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

    private Member getMemberIdByDiscordInfo(DiscordInfoResponse userInfo) {
        String discordId = userInfo.getUserResponse().getId();
        return memberRepository.findByDiscordId(discordId)
                .orElse(null);
    }

    public List<Role> getMemberCurrentRolesByMemberId(Long memberId) {
        return getMemberRolesByMemberId(memberId).stream()
                .map(MemberRole::getRole)
                .filter(role -> role.getBatch() == constantProperties.getCurrentBatch())
                .toList();
    }

    public List<Role> getAllRolesByMemberId(Long memberId) {
        return getMemberRolesByMemberId(memberId).stream()
                .map(MemberRole::getRole)
                .toList();
    }

}