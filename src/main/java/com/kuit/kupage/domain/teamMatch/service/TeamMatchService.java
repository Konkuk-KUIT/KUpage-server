package com.kuit.kupage.domain.teamMatch.service;

import com.kuit.kupage.common.constant.ConstantProperties;
import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.memberRole.service.MemberRoleService;
import com.kuit.kupage.domain.project.entity.AppType;
import com.kuit.kupage.domain.teamMatch.ApplicantStatus;
import com.kuit.kupage.domain.teamMatch.Part;
import com.kuit.kupage.domain.teamMatch.Team;
import com.kuit.kupage.domain.teamMatch.TeamApplicant;
import com.kuit.kupage.domain.teamMatch.converter.ApplyTimeConverter;
import com.kuit.kupage.domain.teamMatch.dto.*;
import com.kuit.kupage.domain.teamMatch.repository.TeamApplicantRepository;
import com.kuit.kupage.domain.teamMatch.repository.TeamRepository;
import com.kuit.kupage.exception.AuthException;
import com.kuit.kupage.exception.KupageException;
import com.kuit.kupage.exception.TeamException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kuit.kupage.common.response.ResponseCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TeamMatchService {

    private final MemberRoleService memberService;
    private final TeamRepository teamRepository;
    private final TeamApplicantRepository teamApplicantRepository;
    private final ConstantProperties constantProperties;

    public TeamMatchResponse apply(Long memberId, Long teamId, TeamMatchRequest request) {
        Member member = memberService.getMember(memberId);
        Team team = getTeam(teamId);
        ApplicantStatus status = constantProperties.getApplicantStatus();
        TeamApplicant applicant = new TeamApplicant(request, member, team, status);
        try {
            if (teamApplicantRepository.countByMemberAndBatchAndStatus(member, status) >= 2) {
                throw new KupageException(EXCEEDED_TEAM_APPLY_LIMIT);
            }
            member.increaseApplyCount();
            TeamApplicant saved = teamApplicantRepository.save(applicant);
            return new TeamMatchResponse(saved.getId());
        } catch (DataIntegrityViolationException e) {
            throw new KupageException(DUPLICATED_TEAM_APPLY);
        } catch (OptimisticLockingFailureException e) {
            throw new KupageException(TEAM_APPLY_FAILED);
        }
    }

    public TeamApplicantResponse getTeamApplicant(Long memberId, Long teamId, boolean isAdmin) {
        Team team = getOwnTeam(memberId, teamId, isAdmin);

        //팀 : 제목, 사람, 역할, 안드인지웹인지, 설명(소제목, 설명)
        String serviceName = team.getServiceName();
        String nameAndPart = team.getOwnerName() + " - " + team.getBatch().getDescription() + " " + Part.PM.name();
        AppType appType = team.getAppType();
        String topicSummary = team.getTopicSummary();
        String mvpFeatures = team.getFeatureRequirements();

        // 각파트별 누가 지원했는지 : 이름, 현재역할, 파트
        // 지원 상세 : 포폴, 지원동기
        List<ApplicantInfo> applicantInfos = parseApplicantInfo(team);

        Map<Part, List<ApplicantInfo>> collected = collectPart(applicantInfos);

        return new TeamApplicantResponse(teamId, serviceName, nameAndPart, appType, topicSummary, mvpFeatures, new ApplicantMap(collected));

    }

    public List<TeamApplicantOverviewDto> getAllCurrentBatchTeamApplicants() {
        List<Team> teams = teamRepository.findAllWithTeamApplicantAndMemberByBatch(constantProperties.getCurrentBatch());

        if (teams.isEmpty()) {
            throw new TeamException(NONE_TEAM);
        }

        return teams.stream().map(this::parseTeamApplicantOverviewDto).toList();

    }

    public TeamApplicantOverviewDto getCurrentBatchOwnTeam(Long memberId) {
        Team team = teamRepository.findByOwnerIdAndBatch(memberId, constantProperties.getCurrentBatch())
                .orElseThrow(() -> new TeamException(NONE_OWN_TEAM));

        return parseTeamApplicantOverviewDto(team);
    }

    public TeamOverviewDto getCurrentBatchAppliedTeam(Long memberId) {
        Team team = teamRepository.findTeamsByMemberIdAndBatch(memberId, constantProperties.getCurrentBatch())
                .orElseThrow(() -> new TeamException(NONE_APPLIED_TEAM));

        List<TeamApplicant> memberTeamApplicant = team.getTeamApplicants().stream()
                .filter(ta -> ta.getMember().getId().equals(memberId))
                .toList();

        long appliedCountWithoutReject = memberTeamApplicant.stream()
                .filter(TeamApplicant::isRejected)
                .count();

        if (appliedCountWithoutReject == memberTeamApplicant.size()) {
            throw new TeamException(REJECTED_TEAM_MATCH);
        }

        Long teamId = team.getId();
        String serviceName = team.getServiceName();
        String topicSummary = team.getTopicSummary();
        // 오너를 아예 멤버:팀으로 나눌지?
        String ownerNameAndPart = team.getOwnerName() + " - " + team.getBatch().getDescription() + " " + Part.PM.name();
        AppType appType = team.getAppType();

        return new TeamOverviewDto(teamId, serviceName, topicSummary, ownerNameAndPart, appType);
    }

    private TeamApplicantOverviewDto parseTeamApplicantOverviewDto(Team team) {
        Long teamId = team.getId();
        String serviceName = team.getServiceName();
        String topicSummary = team.getTopicSummary();
        // 오너를 아예 멤버:팀으로 나눌지?
        String ownerNameAndPart = team.getOwnerName() + " - " + team.getBatch().getDescription() + " " + Part.PM.name();
        AppType appType = team.getAppType();

        List<ApplicantInfo> applicantInfos = parseApplicantInfo(team);
        Map<Part, List<ApplicantInfo>> partListMap = collectPart(applicantInfos);

        int AndroidApplicantNum = partListMap.getOrDefault(Part.Android, List.of()).size();
        int iosApplicantNum = partListMap.getOrDefault(Part.iOS, List.of()).size();
        int webApplicantNum = partListMap.getOrDefault(Part.Web, List.of()).size();
        int serverApplicantNum = partListMap.getOrDefault(Part.Server, List.of()).size();
        int designApplicantNum = partListMap.getOrDefault(Part.Design, List.of()).size();

        return new TeamApplicantOverviewDto(teamId, serviceName, ownerNameAndPart, appType, topicSummary, AndroidApplicantNum, iosApplicantNum, webApplicantNum, serverApplicantNum, designApplicantNum);
    }

    private Map<Part, List<ApplicantInfo>> collectPart(List<ApplicantInfo> applicantInfos) {
        return applicantInfos.stream()
                .collect(Collectors.groupingBy(ApplicantInfo::part));
    }

    private List<ApplicantInfo> parseApplicantInfo(Team team) {
        List<TeamApplicant> teamApplicants = team.getTeamApplicants();

        List<ApplicantInfo> applicantInfos = teamApplicants.stream()
                .filter(ta -> !ta.isRejected())
                .map(ta -> {
                    Member applicantMember = ta.getMember();
                    String applicantMemberNameAndPart = applicantMember.getName() + " - " + team.getBatch().getDescription() + " " + ta.getAppliedPart();
                    Part appliedPart = ta.getAppliedPart();

                    String portfolioUrl = ta.getPortfolioUrl();
                    String formattedTimetable = ApplyTimeConverter.formatTimetable(ta.getCreatedAt());
                    ApplicantDetail applicantDetail = new ApplicantDetail(portfolioUrl);

                    return new ApplicantInfo(applicantMember.getId(), applicantMemberNameAndPart, appliedPart, formattedTimetable, applicantDetail);
                }).toList();

        return applicantInfos;
    }

    private Team getOwnTeam(Long memberId, Long teamId, boolean isAdmin) {
        Team team = teamRepository.findAllWithTeamApplicantAndMemberById(teamId)
                .orElseThrow(() -> new KupageException(NONE_TEAM));

        if (!isAdmin) {
            isTeamOwner(memberId, team);
        }

        return team;
    }

    private void isTeamOwner(Long memberId, Team team) {
        if (!team.getOwnerId().equals(memberId)) {
            throw new AuthException(FORBIDDEN);
        }
    }

    private Team getTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new KupageException(ResponseCode.NONE_TEAM));
    }

    public IdeaRegisterResponse register(Long memberId, IdeaRegisterRequest request) {
        Member owner = memberService.getMember(memberId);
        Team team = new Team(owner.getId(), owner.getName(), constantProperties.getCurrentBatch(), request);
        Team saved = teamRepository.save(team);
        return new IdeaRegisterResponse(saved.getId());
    }

    public AllTeamsResponse getAllTeamIdeas() {
        List<Team> teams = teamRepository.findAllByBatch(constantProperties.getCurrentBatch());
        return AllTeamsResponse.from(teams);
    }
}
