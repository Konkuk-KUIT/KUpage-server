package com.kuit.kupage.unit.teamMatch.service;

import com.kuit.kupage.common.constant.ConstantProperties;
import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.member.service.MemberService;
import com.kuit.kupage.domain.memberRole.MemberRole;
import com.kuit.kupage.domain.memberRole.service.MemberRoleService;
import com.kuit.kupage.domain.project.entity.AppType;
import com.kuit.kupage.domain.role.Role;
import com.kuit.kupage.domain.teamMatch.ApplicantStatus;
import com.kuit.kupage.domain.teamMatch.Part;
import com.kuit.kupage.domain.teamMatch.Team;
import com.kuit.kupage.domain.teamMatch.TeamApplicant;
import com.kuit.kupage.domain.teamMatch.dto.IdeaRegisterRequest;
import com.kuit.kupage.domain.teamMatch.dto.IdeaRegisterResponse;
import com.kuit.kupage.domain.teamMatch.dto.TeamMatchRequest;
import com.kuit.kupage.domain.teamMatch.dto.TeamMatchResponse;
import com.kuit.kupage.domain.teamMatch.repository.TeamApplicantRepository;
import com.kuit.kupage.domain.teamMatch.repository.TeamRepository;
import com.kuit.kupage.domain.teamMatch.service.TeamMatchService;
import com.kuit.kupage.exception.AuthException;
import com.kuit.kupage.exception.KupageException;
import com.kuit.kupage.exception.TeamException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static com.kuit.kupage.common.response.ResponseCode.FORBIDDEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class TeamMatchServiceTest {

    @Mock
    private MemberService memberService;
    @Mock
    private MemberRoleService memberRoleService;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TeamApplicantRepository teamApplicantRepository;
    @Mock
    private ConstantProperties constantProperties;
    @InjectMocks
    private TeamMatchService teamMatchService;

    private Member mockMember;
    private Team mockTeam;
    private TeamApplicant mockApplicant;
    private Role mockRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMember = mock(Member.class);
        mockTeam = mock(Team.class);
        mockApplicant = mock(TeamApplicant.class);
        mockRole = mock(Role.class);
        when(mockRole.getName()).thenReturn("server");

        when(mockApplicant.getId()).thenReturn(1L);

        given(constantProperties.getApplicantStatus()).willReturn(ApplicantStatus.ROUND1_APPLYING);
        given(teamApplicantRepository.findSlotNosByMemberAndStatus(any(), any(), any()))
                .willReturn(List.of());
        given(memberRoleService.getMemberCurrentRolesByMemberId(anyLong()))
                .willReturn(List.of(mockRole));
    }

    @Test
    @DisplayName("팀 매칭 신청 성공 시 TeamApplicant가 저장되고 응답이 반환된다")
    void apply_success() {
        // given
        TeamMatchRequest request = new TeamMatchRequest(
                Part.Server,
                "백엔드 개발자로서 팀에 기여하고 싶습니다.",
                "https://portfolio.com/jihun"
        );

        given(memberService.getMember(1L)).willReturn(mockMember);
        given(teamRepository.findById(1L)).willReturn(Optional.of(mockTeam));
        given(teamApplicantRepository.save(any(TeamApplicant.class))).willReturn(mockApplicant);

        // when
        TeamMatchResponse response = teamMatchService.apply(1L, 1L, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.teamApplicantId()).isEqualTo(1L);

        verify(memberService).getMember(1L);
        verify(teamRepository).findById(1L);
        verify(teamApplicantRepository).save(any(TeamApplicant.class));
    }

    @Test
    @DisplayName("존재하지 않는 팀에 지원 시 KupageException(NONE_TEAM) 발생")
    void apply_fail_whenTeamNotFound() {
        // given
        TeamMatchRequest request = new TeamMatchRequest(
                Part.Server,
                "백엔드 개발자로서 팀에 기여하고 싶습니다.",
                "https://portfolio.com/jihun"
        );
        given(memberService.getMember(1L)).willReturn(mockMember);
        given(teamRepository.findById(99L)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> teamMatchService.apply(1L, 99L, request))
                .isInstanceOf(KupageException.class)
                .hasMessageContaining(ResponseCode.NONE_TEAM.getMessage());

        verify(teamRepository).findById(99L);
        verify(teamApplicantRepository, never()).save(any());
    }

    @Test
    @DisplayName("팀 지원 상세 조회 시 팀이 존재하지 않으면 KupageException(NONE_TEAM)이 발생한다")
    void get_team_applicant_should_throw_kupage_exception_when_team_not_found() {
        // given
        Long memberId = 1L;
        Long teamId = 99L;
        boolean isAdmin = false;

        given(teamRepository.findAllWithTeamApplicantAndMemberById(teamId))
                .willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> teamMatchService.getTeamApplicantByMemberAndTeam(memberId, teamId, isAdmin))
                .isInstanceOf(KupageException.class)
                .hasMessageContaining(ResponseCode.NONE_TEAM.getMessage());

        verify(teamRepository).findAllWithTeamApplicantAndMemberById(teamId);
        verifyNoMoreInteractions(teamApplicantRepository);
    }

    @Test
    @DisplayName("팀 지원 상세 조회 시 팀장이 아니고 관리자도 아니면 AuthException(FORBIDDEN)이 발생한다")
    void get_team_applicant_should_throw_auth_exception_when_not_owner_and_not_admin() {
        // given
        Long memberId = 1L;
        Long teamId = 10L;
        boolean isAdmin = false;

        Team team = mock(Team.class);
        given(teamRepository.findAllWithTeamApplicantAndMemberById(teamId))
                .willReturn(Optional.of(team));
        given(team.getOwnerId()).willReturn(2L); // memberId와 다른 소유자

        // when / then
        assertThatThrownBy(() -> teamMatchService.getTeamApplicantByMemberAndTeam(memberId, teamId, isAdmin))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(FORBIDDEN.getMessage());

        verify(teamRepository).findAllWithTeamApplicantAndMemberById(teamId);
    }

    @Test
    @DisplayName("현재 기수 팀 지원 목록 조회 시 팀이 하나도 없으면 TeamException(NONE_TEAM)이 발생한다")
    void get_all_current_batch_team_applicants_should_throw_team_exception_when_no_team() {
        // given
        given(constantProperties.getCurrentBatch()).willReturn(null);
        given(teamRepository.findAllWithTeamApplicantAndMemberByBatch(constantProperties.getCurrentBatch()))
                .willReturn(List.of());

        // when / then
        assertThatThrownBy(() -> teamMatchService.getAllCurrentBatchTeamApplicants())
                .isInstanceOf(TeamException.class)
                .hasMessageContaining(ResponseCode.NONE_TEAM.getMessage());

        verify(teamRepository).findAllWithTeamApplicantAndMemberByBatch(constantProperties.getCurrentBatch());
    }

    @Test
    @DisplayName("현재 기수 내에서 자신이 소유한 팀이 없으면 TeamException(NONE_OWN_TEAM)이 발생한다")
    void get_current_batch_team_applicants_should_throw_team_exception_when_no_own_team() {
        // given
        Long memberId = 1L;
        given(constantProperties.getCurrentBatch()).willReturn(null);
        given(teamRepository.findByOwnerIdAndBatch(memberId, constantProperties.getCurrentBatch()))
                .willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> teamMatchService.getCurrentBatchOwnTeam(memberId))
                .isInstanceOf(TeamException.class)
                .hasMessageContaining(ResponseCode.NONE_OWN_TEAM.getMessage());

        verify(teamRepository).findByOwnerIdAndBatch(memberId, constantProperties.getCurrentBatch());
    }

    @Test
    @DisplayName("아이디어 등록 시 Team이 저장되고 IdeaRegisterResponse가 반환된다")
    void register_should_save_team_and_return_response() {
        // given
        Long memberId = 1L;
        Member owner = mock(Member.class);
        given(owner.getId()).willReturn(memberId);
        given(owner.getName()).willReturn("홍길동");

        given(memberService.getMember(memberId)).willReturn(owner);
        given(constantProperties.getCurrentBatch()).willReturn(null);

        Team savedTeam = mock(Team.class);
        given(savedTeam.getId()).willReturn(100L);
        given(teamRepository.save(any(Team.class))).willReturn(savedTeam);
        boolean isAdmin = false;

        IdeaRegisterRequest request = new IdeaRegisterRequest(
                "서비스 이름",
                AppType.Android,
                "토픽 요약",
                "https://image-url.com",
                "https://intro-file-url.com",
                "주요 기능",
                "이런 개발자분이 오시면 좋겠습니다"
        );

        // when
        IdeaRegisterResponse response = teamMatchService.register(memberId, request, isAdmin);

        // then
        assertThat(response).isNotNull();
        assertThat(response.teamId()).isEqualTo(100L);

        verify(memberService).getMember(memberId);
        verify(teamRepository).save(any(Team.class));
    }


    // TODO. 아이디어 등록 시 PM 부원은 한 기수당 하나의 아이디어만 등록 가능하다
    // TODO. 아이디어 등록 시 운영진은 한 기수당 여러 개의 아이디어를 등록 가능하다
}