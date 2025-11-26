package com.kuit.kupage.domain.teamMatch.controller;

import com.kuit.kupage.common.auth.AllowedParts;
import com.kuit.kupage.common.auth.AuthMember;
import com.kuit.kupage.common.constant.ConstantProperties;
import com.kuit.kupage.common.response.BaseResponse;
import com.kuit.kupage.common.swagger.SwaggerErrorResponse;
import com.kuit.kupage.common.swagger.SwaggerErrorResponses;
import com.kuit.kupage.domain.teamMatch.Part;
import com.kuit.kupage.domain.teamMatch.dto.*;
import com.kuit.kupage.domain.teamMatch.service.TeamMatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping
@RestController
@Tag(name = "Team Match", description = "팀매칭 신청·아이디어 등록·지원 현황 조회 등 팀매칭 관련 기능을 제공합니다.")
public class TeamMatchController {

    private final TeamMatchService teamMatchService;
    private final ConstantProperties constantProperties;

    @GetMapping("/teams/applications")
    @Operation(
            summary = "팀매칭 지원 현황 조회",
            description = """
                    로그인한 사용자의 역할에 따라 팀매칭 지원 현황을 조회합니다.
                    * 운영진(Admin): 현재 기수의 모든 팀에 대한 전체 지원 현황을 조회합니다.
                    * PM: 내가 속한 팀에 지원한 지원자 목록 및 현황을 조회합니다.
                    * 개발자/디자이너 등 일반 참여자: 내가 지원한 팀 목록 및 각 팀에 대한 지원 상태를 조회합니다.
                    """
    )
    @SwaggerErrorResponses(SwaggerErrorResponse.TEAM_MATCH_STATUS)
    public BaseResponse<?> applicationStatus(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthMember authMember,
            @RequestAttribute("role") String role) {

        if (authMember.isAdmin()) {
            List<TeamApplicantOverviewDto> allCurrentBatchTeamApplicants = teamMatchService.getAllCurrentBatchTeamApplicants();
            return new BaseResponse<>(allCurrentBatchTeamApplicants);
        }

        Long id = authMember.getId();

        if (role.equals(Part.PM.name())) {
            return new BaseResponse<>(teamMatchService.getCurrentBatchOwnTeam(id));
        }

        if (LocalDateTime.now().isAfter(constantProperties.getFinalResultTime())) {
            return new BaseResponse<>(teamMatchService.getFinalResultTeamMatching(id));
        }

        return new BaseResponse<>(teamMatchService.getCurrentBatchAppliedTeam(id));
    }


    @AllowedParts(Part.PM)
    @GetMapping("/teams/{teamId}/applications")
    @Operation(
            summary = "특정 팀 지원자 목록 조회 (PM 전용)",
            description = "특정 팀에 지원한 지원자들의 상세 리스트를 조회합니다. PM 또는 운영진 권한이 필요합니다."
    )
    @SwaggerErrorResponses(SwaggerErrorResponse.TEAM_MATCH_APPLICANT)
    public BaseResponse<TeamApplicantResponse> getApplications(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthMember authMember,
            @PathVariable("teamId") Long teamId) {

        Long memberId = authMember.getId();
        boolean isAdmin = authMember.isAdmin();

        return new BaseResponse<>(teamMatchService.getTeamApplicant(memberId, teamId, isAdmin));
    }

    @PostMapping("/teams/{teamId}/match")
    @Operation(summary = "팀매칭 지원 신청",
            description = "선택한 팀에 팀매칭 지원을 신청합니다. 이미 지원한 팀인 경우 비즈니스 로직에 따라 예외가 발생할 수 있습니다.")
    @SwaggerErrorResponses()
    public BaseResponse<?> apply(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthMember authMember,
            @PathVariable(name = "teamId") Long teamId,
            @Validated @RequestBody TeamMatchRequest request) {
        Long memberId = authMember.getId();
        log.info("[apply] memberId = {}, teamId = {}, 팀매칭 지원 request = {}", memberId, teamId, request.toString());
        TeamMatchResponse response = teamMatchService.apply(memberId, teamId, request);
        return new BaseResponse<>(response);
    }

    @AllowedParts(Part.PM)
    @PostMapping("/ideas")
    @Operation(summary = "팀 아이디어 등록",
            description = "현재 기수에서 사용할 팀 아이디어를 등록합니다. PM이 자신의 팀 아이디어를 등록할 때 사용합니다.")
    @SwaggerErrorResponses(SwaggerErrorResponse.AUTH_COMMON)
    public BaseResponse<?> registerIdea(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthMember authMember,
            @Validated @RequestBody IdeaRegisterRequest request) {
        Long memberId = authMember.getId();
        log.debug("[registerIdea] memberId = {}, 팀매칭 지원 request = {}", memberId, request.toString());
        IdeaRegisterResponse response = teamMatchService.register(memberId, request);
        return new BaseResponse<>(response);
    }

    @GetMapping("/teams")
    @Operation(summary = "전체 팀 아이디어 목록 조회",
            description = "현재 기수에 속한 모든 팀의 아이디어/서비스 정보를 조회합니다. 현재 기수 참여자만 접근할 수 있습니다.")
    @SwaggerErrorResponses(SwaggerErrorResponse.TEAM_MATCH_VIEW)
    public BaseResponse<AllTeamsResponse> getTeamIdeas() {
        AllTeamsResponse response = teamMatchService.getAllTeamIdeas();
        return new BaseResponse<>(response);
    }
}
