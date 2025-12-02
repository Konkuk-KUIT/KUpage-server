package com.kuit.kupage.domain.teamMatch.service;

import com.kuit.kupage.common.constant.ConstantProperties;
import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.member.service.MemberService;
import com.kuit.kupage.domain.memberRole.service.MemberRoleService;
import com.kuit.kupage.domain.project.domain.AppType;
import com.kuit.kupage.domain.role.Role;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.kuit.kupage.common.response.ResponseCode.*;
import static com.kuit.kupage.domain.teamMatch.ApplicantStatus.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TeamMatchService {

    private final MemberService memberService;
    private final TeamRepository teamRepository;
    private final TeamApplicantRepository teamApplicantRepository;
    private final ConstantProperties constantProperties;
    private final MemberRoleService memberRoleService;

    public TeamMatchResponse apply(Long memberId, Long teamId, TeamMatchRequest request) {
        Member member = memberService.getMember(memberId);
        isValidPart(member, request.appliedPart());
        Team team = getTeam(teamId);
        ApplicantStatus status = constantProperties.getApplicantStatus();
        Batch batch = constantProperties.getCurrentBatch();
        int slotNo = resolveSlotNo(member, status, batch);
        TeamApplicant applicant = new TeamApplicant(request, member, team, status, slotNo, batch);
        try {
            TeamApplicant saved = teamApplicantRepository.save(applicant);
            return new TeamMatchResponse(saved.getId());
        } catch (DataIntegrityViolationException e) {
            Throwable cause = e.getCause();
            if (cause instanceof org.hibernate.exception.ConstraintViolationException cve) {
                String constraintName = cve.getConstraintName();
                if (constraintName != null) {
                    String normalizedName = constraintName.toLowerCase(Locale.ROOT);
                    if (normalizedName.contains("uk_status_team_applicant_member_team")) {
                        throw new KupageException(DUPLICATED_TEAM_APPLY);
                    }
                    if (normalizedName.contains("uk_member_status_slot")) {
                        throw new KupageException(EXCEEDED_TEAM_APPLY_LIMIT);
                    }
                }
            }
            throw new KupageException(TEAM_APPLY_FAILED);
        } catch (OptimisticLockingFailureException e) {
            throw new KupageException(TEAM_APPLY_FAILED);
        }
    }

    private void isValidPart(Member member, Part appliedPart) {
        List<Role> currentRoles = memberRoleService.getMemberCurrentRolesByMemberId(member.getId());
        String appliedPartName = appliedPart.name().toLowerCase(Locale.ROOT);

        boolean hasRoleForAppliedPart = currentRoles.stream()
                .map(Role::getName)
                .map(name -> name.toLowerCase(Locale.ROOT))
                .anyMatch(roleName -> roleName.contains(appliedPartName));

        if (!hasRoleForAppliedPart) {
            throw new KupageException(INVALID_APPLY_PART);
        }
    }

    @Transactional(readOnly = true)
    public TeamApplicantResponse getTeamApplicantByMemberAndTeam(Long memberId, Long teamId, boolean isAdmin) {
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

    @Transactional(readOnly = true)
    public List<TeamApplicantOverviewDto> getAllCurrentBatchTeamApplicants() {
        List<Team> teams = teamRepository.findAllWithTeamApplicantAndMemberByBatch(constantProperties.getCurrentBatch());

        if (teams.isEmpty()) {
            throw new TeamException(NONE_TEAM);
        }

        return teams.stream()
                .map(this::parseTeamApplicantOverviewDto)
                .toList();

    }

    @Transactional(readOnly = true)
    public TeamApplicantOverviewDto getCurrentBatchOwnTeam(Long memberId) {
        Team team = teamRepository.findByOwnerIdAndBatch(memberId, constantProperties.getCurrentBatch())
                .orElseThrow(() -> new TeamException(NONE_OWN_TEAM));

        return parseTeamApplicantOverviewDto(team);
    }

    @Transactional(readOnly = true)
    public TeamOverviewDto getCurrentBatchAppliedTeam(Long memberId) {

        List<TeamApplicant> memberTeamApplicants = findMyTeamApplicants(memberId);

        long appliedCountWithoutReject = memberTeamApplicants.stream()
                .filter(TeamApplicant::isRejected)
                .count();

        if (appliedCountWithoutReject == memberTeamApplicants.size()) {
            throw new TeamException(REJECTED_TEAM_MATCH);
        }

        Team team = memberTeamApplicants.stream()
                .filter(ta -> !ta.isRejected())
                .findFirst()
                .map(TeamApplicant::getTeam)
                .orElseThrow(() -> new TeamException(REJECTED_TEAM_MATCH));

        Long teamId = team.getId();
        String serviceName = team.getServiceName();
        String topicSummary = team.getTopicSummary();
        // 오너를 아예 멤버:팀으로 나눌지?
        String ownerNameAndPart = team.getOwnerName() + " - " + team.getBatch().getDescription() + " " + Part.PM.name();
        AppType appType = team.getAppType();

        return new TeamOverviewDto(teamId, serviceName, topicSummary, ownerNameAndPart, appType);
    }

    private List<TeamApplicant> findMyTeamApplicants(Long memberId) {
        // 지원한 모든 팀 조회
        List<Team> allTeamsByMemberIdAndBatch = teamRepository.findAllTeamsByMemberIdAndBatch(memberId, constantProperties.getCurrentBatch());

        if (allTeamsByMemberIdAndBatch.isEmpty()) {
            throw new TeamException(NONE_APPLIED_TEAM);
        }

        // 팀의 지원자 중 자신의 지원 정보만 추출
        return allTeamsByMemberIdAndBatch.stream().flatMap(t -> t.getTeamApplicants().stream()).filter(ta -> ta.getMember().getId().equals(memberId)).toList();
    }

    @Transactional(readOnly = true)
    public TeamApplicantOverviewDto getFinalResultTeamMatching(Long memberId) {

        List<TeamApplicant> memberTeamApplicants = findMyTeamApplicants(memberId);

        // 최종 확정된 지원 정보
        TeamApplicant teamApplicant = memberTeamApplicants.stream()
                .filter(ta -> ta.getStatus().equals(FINAL_CONFIRMED))
                .findFirst()
                .orElseThrow(() -> new TeamException(REJECTED_TEAM_MATCH));

        return parseTeamApplicantOverviewDto(teamApplicant.getTeam());
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

    private int resolveSlotNo(Member member, ApplicantStatus status, Batch batch) {
        List<Integer> usedSlots = teamApplicantRepository.findSlotNosByMemberAndStatus(member, status, batch);

        log.info("usedSlots = {}", usedSlots);
        if (status == ROUND1_APPLYING && !usedSlots.contains(1)) {
            return 1;
        }
        if (status == ROUND2_APPLYING && !usedSlots.contains(2)) {
            return 2;
        }
        throw new KupageException(EXCEEDED_TEAM_APPLY_LIMIT);
    }

    private Map<Part, List<ApplicantInfo>> collectPart(List<ApplicantInfo> applicantInfos) {
        return applicantInfos.stream().collect(Collectors.groupingBy(ApplicantInfo::part));
    }

    private List<ApplicantInfo> parseApplicantInfo(Team team) {
        List<TeamApplicant> teamApplicants = team.getTeamApplicants();

        return teamApplicants.stream().filter(ta -> !ta.isRejected()).map(ta -> {
            Member applicantMember = ta.getMember();
            String applicantMemberNameAndPart = applicantMember.getName() + " - " + team.getBatch().getDescription() + " " + ta.getAppliedPart();
            Part appliedPart = ta.getAppliedPart();

            String portfolioUrl = ta.getPortfolioUrl();
            String motivation = ta.getMotivation();
            String formattedTimetable = ApplyTimeConverter.formatTimetable(ta.getCreatedAt());
            ApplicantDetail applicantDetail = new ApplicantDetail(portfolioUrl, motivation);

            return new ApplicantInfo(applicantMember.getId(), applicantMemberNameAndPart, appliedPart, formattedTimetable, applicantDetail);
        }).toList();
    }

    private Team getOwnTeam(Long memberId, Long teamId, boolean isAdmin) {
        Team team = teamRepository.findAllWithTeamApplicantAndMemberById(teamId).orElseThrow(() -> new KupageException(NONE_TEAM));

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
        return teamRepository.findById(teamId).orElseThrow(() -> new KupageException(ResponseCode.NONE_TEAM));
    }

    public IdeaRegisterResponse register(Long memberId, IdeaRegisterRequest request, boolean isAdmin) {
        Member owner = memberService.getMember(memberId);
        Batch batch = constantProperties.getCurrentBatch();
        validateTeamRegisterPermission(isAdmin, owner, batch);
        Team team = teamRepository.save(new Team(owner.getId(), owner.getName(), batch, request));
        return new IdeaRegisterResponse(team.getId());
    }

    private void validateTeamRegisterPermission(boolean isAdmin, Member owner, Batch batch) {
        Optional<Team> existingTeam = teamRepository.findByOwnerIdAndBatch(owner.getId(), batch);
        if (!isAdmin && existingTeam.isPresent()) {
            throw new KupageException(PM_PROJECT_LIMIT_EXCEEDED);
        }
    }

    @Transactional(readOnly = true)
    public AllTeamsResponse getAllTeamIdeas(Long memberId) {
        List<Team> teams = teamRepository.findAllByBatch(constantProperties.getCurrentBatch());
        List<TeamApplicant> teamApplicants = teamApplicantRepository.findByMember_IdAndTeam_Batch(memberId, constantProperties.getCurrentBatch());
        try {
            LocalDateTime now = LocalDateTime.now();
            log.info("[getAllTeamIdeas] now = {}", now);
            boolean canApply = canApply(teamApplicants);
            return AllTeamsResponse.from(teams, canApply);
        } catch (KupageException e) {
            return AllTeamsResponse.from(teams, false);
        }
    }

    private boolean canApply(List<TeamApplicant> teamApplicants) {
        ApplicantStatus currentStatus = constantProperties.getApplicantStatus();

        // 이미 최종 확정된 팀이 있으면 추가 지원 불가
        boolean hasFinalConfirmed = teamApplicants.stream()
                .anyMatch(ta -> ta.getStatus() == FINAL_CONFIRMED);
        if (hasFinalConfirmed) {
            return false;
        }

        // 현재 모집 라운드에서 이미 지원한 상태라면 추가 지원 불가
        if (currentStatus == ROUND1_APPLYING) {
            boolean alreadyAppliedRound1 = teamApplicants.stream()
                    .anyMatch(ta -> ta.getStatus() == ROUND1_APPLYING);
            if (alreadyAppliedRound1) {
                return false;
            }
        }

        if (currentStatus == ROUND2_APPLYING) {
            boolean alreadyAppliedRound2 = teamApplicants.stream()
                    .anyMatch(ta -> ta.getStatus() == ROUND2_APPLYING);
            return !alreadyAppliedRound2;
        }

        return true;
    }

    public void acceptTeamApplication(Long teamApplicantId) {
        TeamApplicant teamApplicant = getTeamApplicantById(teamApplicantId);
        teamApplicant.accept();
    }

    public void rejectTeamApplicant(Long teamApplicantId) {
        TeamApplicant teamApplicant = getTeamApplicantById(teamApplicantId);
        teamApplicant.reject();
    }

    private TeamApplicant getTeamApplicantById(Long teamApplicantId) {
        return teamApplicantRepository.findById(teamApplicantId)
                .orElseThrow(() -> new KupageException(NONE_APPLICANT));
    }
}
