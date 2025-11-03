package com.kuit.kupage.domain.teamMatch.controller;

import com.kuit.kupage.common.auth.AuthMember;
import com.kuit.kupage.common.auth.AuthRole;
import com.kuit.kupage.common.response.BaseResponse;
import com.kuit.kupage.domain.member.service.MemberService;
import com.kuit.kupage.domain.memberRole.service.MemberRoleService;
import com.kuit.kupage.domain.teamMatch.dto.*;
import com.kuit.kupage.domain.teamMatch.service.TeamMatchService;
import com.kuit.kupage.exception.AuthException;
import com.kuit.kupage.exception.KupageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.kuit.kupage.common.response.ResponseCode.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping
@RestController
public class TeamMatchController {

    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024;         // 20MB

    private final MemberService memberService;
    private final MemberRoleService memberRoleService;
    private final TeamMatchService teamMatchService;

    @GetMapping("/teams/applications")
    public BaseResponse<?> applicationStatus(@AuthenticationPrincipal AuthMember authMember) {

        Long memberId = authMember.getId();
        isCurrentMember(memberId);

        if (isAdmin(authMember)) {
            List<TeamApplicantOverviewDto> allCurrentBatchTeamApplicants = teamMatchService.getAllCurrentBatchTeamApplicants();
            return new BaseResponse<>(allCurrentBatchTeamApplicants);
        }


        if (!isPm(authMember)) {
            throw new AuthException(FORBIDDEN);
        }

        return new BaseResponse<>(teamMatchService.getCurrentBatchTeamApplicants(authMember.getId()));
    }


    @GetMapping("/teams/{teamId}/applications")
    public BaseResponse<TeamApplicantResponse> getApplications(
            @AuthenticationPrincipal AuthMember authMember,
            @PathVariable("teamId") Long teamId) {

        Long memberId = authMember.getId();

        boolean isAdmin = isAdmin(authMember);
        boolean isPm = isPm(authMember);

        if (!(isAdmin || isPm)) {
            throw new AuthException(FORBIDDEN);
        }

        return new BaseResponse<>(teamMatchService.getTeamApplicant(memberId, teamId, isAdmin));
    }

    @PostMapping("/teams/{teamId}/match")
    public BaseResponse<?> apply(
            @AuthenticationPrincipal AuthMember authMember,
            @PathVariable(name = "teamId") Long teamId,
            @Validated @RequestBody TeamMatchRequest request) {

        Long memberId = authMember.getId();

        isCurrentMember(memberId);

        log.info("[apply] memberId = {}, teamId = {}, 팀매칭 지원 request = {}", memberId, teamId, request.toString());
        TeamMatchResponse response = teamMatchService.apply(memberId, teamId, request);
        return new BaseResponse<>(response);
    }

    @PostMapping("/portfolios")
    public BaseResponse<PortfolioUploadResponse> uploadPortfolios(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.error("[uploadPortfolios] 업로드할 파일이 비어있거나 존재하지 않습니다.");
            throw new KupageException(BAD_REQUEST);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            log.error("[uploadPortfolios] 파일 크기가 20MB를 초과했습니다. ({} bytes)", file.getSize());
            throw new KupageException(TOO_BIG_FILE);
        }

        log.debug("[uploadPortfolios] 파일 업로드 요청");
        log.debug(" - 파일 이름: {}", file.getOriginalFilename());
        log.debug(" - 파일 타입: {}", file.getContentType());
        log.debug(" - 파일 크기: {} bytes", file.getSize());
        log.debug(" - 비어있음 여부: {}", file.isEmpty());

        PortfolioUploadResponse response = teamMatchService.uploadPortfolio(file);
        return new BaseResponse<>(response);
    }

    private void isCurrentMember(Long memberId) {
        if (!memberService.isCurrentBatch(memberId)) {
            throw new AuthException(NOT_CURRENT_BATCH_MEMBER);
        }
    }

    private boolean isPm(AuthMember authMember) {
        return memberRoleService.getMemberRolesByMemberId(authMember.getId()).stream()
                .anyMatch(mr -> mr.getRole().getName().contains("PM"));
    }

    private boolean isAdmin(AuthMember authMember) {
        return authMember.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(AuthRole.ADMIN.getRole()));
    }
}
