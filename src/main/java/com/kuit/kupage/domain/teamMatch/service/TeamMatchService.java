package com.kuit.kupage.domain.teamMatch.service;

import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.member.service.MemberService;
import com.kuit.kupage.domain.teamMatch.Team;
import com.kuit.kupage.domain.teamMatch.TeamApplicant;
import com.kuit.kupage.domain.teamMatch.dto.IdeaRegisterRequest;
import com.kuit.kupage.domain.teamMatch.dto.IdeaRegisterResponse;
import com.kuit.kupage.domain.teamMatch.dto.TeamMatchRequest;
import com.kuit.kupage.domain.teamMatch.dto.TeamMatchResponse;
import com.kuit.kupage.domain.teamMatch.repository.TeamApplicantRepository;
import com.kuit.kupage.domain.teamMatch.repository.TeamRepository;
import com.kuit.kupage.exception.KupageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TeamMatchService {
    private final MemberService memberService;
    private final TeamRepository teamRepository;
    private final TeamApplicantRepository teamApplicantRepository;


    public TeamMatchResponse apply(Long memberId, Long teamId, TeamMatchRequest request) {
        Member member = memberService.getMember(memberId);
        Team team = getTeam(teamId);
        TeamApplicant applicant = new TeamApplicant(request, member, team);
        TeamApplicant saved = teamApplicantRepository.save(applicant);
        return new TeamMatchResponse(saved.getId());
    }

    private Team getTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new KupageException(ResponseCode.NONE_TEAM));
    }

    public IdeaRegisterResponse register(Long memberId, IdeaRegisterRequest request) {

        // TODO. member가 PM 또는 운영진인지 인가

        Team team = new Team(request);
        Team saved = teamRepository.save(team);
        return new IdeaRegisterResponse(saved.getId());
    }
}
