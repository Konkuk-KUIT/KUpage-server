package com.kuit.kupage.domain.detail.service;

import com.kuit.kupage.common.auth.AuthTokenResponse;
import com.kuit.kupage.common.jwt.JwtTokenService;
import com.kuit.kupage.domain.detail.Detail;
import com.kuit.kupage.domain.detail.dto.SignupRequest;
import com.kuit.kupage.domain.detail.repository.DetailRepository;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.member.repository.MemberRepository;
import com.kuit.kupage.domain.memberRole.service.MemberRoleService;
import com.kuit.kupage.domain.oauth.dto.LoginOrSignupResult;
import com.kuit.kupage.domain.role.Role;
import com.kuit.kupage.domain.teamMatch.Team;
import com.kuit.kupage.domain.teamMatch.TeamApplicant;
import com.kuit.kupage.domain.teamMatch.repository.TeamApplicantRepository;
import com.kuit.kupage.domain.teamMatch.repository.TeamRepository;
import com.kuit.kupage.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.kuit.kupage.common.response.ResponseCode.ALREADY_MEMBER;
import static com.kuit.kupage.common.response.ResponseCode.NONE_MEMBER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DetailService {

    private final MemberRepository memberRepository;
    private final DetailRepository detailRepository;
    private final MemberRoleService memberRoleService;
    private final JwtTokenService jwtTokenService;
    private final TeamRepository teamRepository;
    private final TeamApplicantRepository teamApplicantRepository;

    @Transactional
    public LoginOrSignupResult signup(SignupRequest signupRequest, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NONE_MEMBER));

        if (member.getDetail() != null) {
            throw new MemberException(ALREADY_MEMBER);
        }

        Detail savedDetail = detailRepository.save(Detail.of(
                signupRequest.name(),
                signupRequest.studentNumber(),
                signupRequest.departName(),
                signupRequest.grade(),
                signupRequest.githubId(),
                signupRequest.email(),
                signupRequest.phoneNumber(),
                signupRequest.birthday()));

        member.updateDetail(signupRequest.name(), savedDetail);
        memberRoleService.updateMemberRoles(member);

        AuthTokenResponse authTokenResponse = jwtTokenService.generateTokens(member);
        List<String> roles = memberRoleService.getMemberCurrentRolesByMemberId(memberId).stream()
                .map(Role::getName)
                .toList();

        Optional<Team> byOwnerNameTeam = teamRepository.findByOwnerName(member.getName());
        byOwnerNameTeam.ifPresent(
                team -> {
                    team.setOwnerId(member.getId());
                }
        );
        Optional<TeamApplicant> byNameTeamApplicant = teamApplicantRepository.findByName(member.getName());
        byNameTeamApplicant.ifPresent(
                teamApplicant -> {
                    teamApplicant.setMember(member);
                }
        );

        return new LoginOrSignupResult(memberId, roles, authTokenResponse);
    }
}
