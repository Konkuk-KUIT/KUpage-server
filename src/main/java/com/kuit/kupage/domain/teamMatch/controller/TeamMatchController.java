package com.kuit.kupage.domain.teamMatch.controller;

import com.kuit.kupage.common.auth.AllowedParts;
import com.kuit.kupage.common.auth.AuthMember;
import com.kuit.kupage.common.response.BaseResponse;
import com.kuit.kupage.domain.teamMatch.Part;
import com.kuit.kupage.domain.teamMatch.dto.*;
import com.kuit.kupage.domain.teamMatch.service.TeamMatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping
@RestController
public class TeamMatchController {

    private final TeamMatchService teamMatchService;

    // todo 결국 멤버 전체 조회 가능하도록 해야함.
    // 운영진 -> 다 보이게
    // PM -> 전체 및 지원 수까지 보이게
    // 개발자, 디자이너 -> 그냥 본인이 지원한 팀 목록만
    @GetMapping("/teams/applications")
    public BaseResponse<?> applicationStatus(
            @AuthenticationPrincipal AuthMember authMember,
            @RequestAttribute("role") String role) {

        if (authMember.isAdmin()) {
            List<TeamApplicantOverviewDto> allCurrentBatchTeamApplicants = teamMatchService.getAllCurrentBatchTeamApplicants();
            return new BaseResponse<>(allCurrentBatchTeamApplicants);
        }

        Long id = authMember.getId();

        if (role.equals(Part.PM.name())) {
            return new BaseResponse<>(teamMatchService.getCurrentBatchOwnTeam(id));
        }

        return new BaseResponse<>(teamMatchService.getCurrentBatchAppliedTeam(id));
    }


    @AllowedParts(Part.PM)
    @GetMapping("/teams/{teamId}/applications")
    public BaseResponse<TeamApplicantResponse> getApplications(
            @AuthenticationPrincipal AuthMember authMember,
            @PathVariable("teamId") Long teamId) {

        Long memberId = authMember.getId();
        boolean isAdmin = authMember.isAdmin();

        return new BaseResponse<>(teamMatchService.getTeamApplicant(memberId, teamId, isAdmin));
    }

    @PostMapping("/teams/{teamId}/match")
    public BaseResponse<?> apply(
            @AuthenticationPrincipal AuthMember authMember,
            @PathVariable(name = "teamId") Long teamId,
            @Validated @RequestBody TeamMatchRequest request) {
        Long memberId = authMember.getId();
        log.info("[apply] memberId = {}, teamId = {}, 팀매칭 지원 request = {}", memberId, teamId, request.toString());
        TeamMatchResponse response = teamMatchService.apply(memberId, teamId, request);
        return new BaseResponse<>(response);
    }

    @PostMapping("/ideas")
    public BaseResponse<?> registerIdea(
            @AuthenticationPrincipal AuthMember authMember,
            @Validated @RequestBody IdeaRegisterRequest request) {
        Long memberId = authMember.getId();
        log.debug("[registerIdea] memberId = {}, 팀매칭 지원 request = {}", memberId, request.toString());
        IdeaRegisterResponse response = teamMatchService.register(memberId, request);
        return new BaseResponse<>(response);
    }
}
