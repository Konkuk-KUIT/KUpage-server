package com.kuit.kupage.domain.teamMatch.service;

import com.kuit.kupage.common.file.S3Service;
import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.member.service.MemberService;
import com.kuit.kupage.domain.teamMatch.Team;
import com.kuit.kupage.domain.teamMatch.TeamApplicant;
import com.kuit.kupage.domain.teamMatch.dto.PortfolioUploadResponse;
import com.kuit.kupage.domain.teamMatch.dto.TeamMatchRequest;
import com.kuit.kupage.domain.teamMatch.dto.TeamMatchResponse;
import com.kuit.kupage.domain.teamMatch.repository.TeamApplicantRepository;
import com.kuit.kupage.domain.teamMatch.repository.TeamRepository;
import com.kuit.kupage.exception.KupageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TeamMatchService {
    private final MemberService memberService;
    private final TeamRepository teamRepository;
    private final TeamApplicantRepository teamApplicantRepository;
    private final S3Service s3Service;


    public TeamMatchResponse apply(Long memberId, Long teamId, TeamMatchRequest request) {
        Member member = memberService.getMember(memberId);
        Team team = getTeam(teamId);
        TeamApplicant applicant = new TeamApplicant(request, member, team);
        TeamApplicant saved = teamApplicantRepository.save(applicant);
        return new TeamMatchResponse(saved.getId());
    }

    public PortfolioUploadResponse uploadPortfolio(MultipartFile file) {
        String uploadPortfolio = s3Service.uploadPortfolio(file);
        log.info("[uploadPortfolio] 업로드 된 포트폴리오 url = {}", uploadPortfolio);
        return new PortfolioUploadResponse(uploadPortfolio);
    }


    private Team getTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new KupageException(ResponseCode.NONE_TEAM));
    }
}
