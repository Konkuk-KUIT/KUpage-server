package com.kuit.kupage.domain.teamMatch.controller;

import com.kuit.kupage.common.auth.AuthMember;
import com.kuit.kupage.common.response.BaseResponse;
import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.domain.teamMatch.dto.PortfolioUploadResponse;
import com.kuit.kupage.domain.teamMatch.dto.TeamMatchRequest;
import com.kuit.kupage.domain.teamMatch.dto.TeamMatchResponse;
import com.kuit.kupage.domain.teamMatch.service.TeamMatchService;
import com.kuit.kupage.exception.KupageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RequestMapping
@RestController
public class TeamMatchController {

    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024;         // 20MB

    private final TeamMatchService teamMatchService;

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

    @PostMapping("/portfolios")
    public BaseResponse<PortfolioUploadResponse> uploadPortfolios(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.error("[uploadPortfolios] 업로드할 파일이 비어있거나 존재하지 않습니다.");
            throw new KupageException(ResponseCode.BAD_REQUEST);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            log.error("[uploadPortfolios] 파일 크기가 20MB를 초과했습니다. ({} bytes)", file.getSize());
            throw new KupageException(ResponseCode.TOO_BIG_FILE);
        }

        log.debug("[uploadPortfolios] 파일 업로드 요청");
        log.debug(" - 파일 이름: {}", file.getOriginalFilename());
        log.debug(" - 파일 타입: {}", file.getContentType());
        log.debug(" - 파일 크기: {} bytes", file.getSize());
        log.debug(" - 비어있음 여부: {}", file.isEmpty());

        PortfolioUploadResponse response = teamMatchService.uploadPortfolio(file);
        return new BaseResponse<>(response);
    }
}
