package com.kuit.kupage.domain.teamMatch.controller;

import com.kuit.kupage.common.auth.AuthMember;
import com.kuit.kupage.common.response.BaseResponse;
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

    @GetMapping("/teams/applications")
    public BaseResponse<?> applicationStatus(@AuthenticationPrincipal AuthMember authMember) {

        if (authMember.isAdmin()) {
            List<TeamApplicantOverviewDto> allCurrentBatchTeamApplicants = teamMatchService.getAllCurrentBatchTeamApplicants();
            return new BaseResponse<>(allCurrentBatchTeamApplicants);
        }

        return new BaseResponse<>(teamMatchService.getCurrentBatchTeamApplicants(authMember.getId()));
    }


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
