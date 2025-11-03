package com.kuit.kupage.domain.teamMatch.service;

import com.kuit.kupage.common.file.S3Service;
import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.member.service.MemberService;
import com.kuit.kupage.domain.teamMatch.Part;
import com.kuit.kupage.domain.teamMatch.Team;
import com.kuit.kupage.domain.teamMatch.TeamApplicant;
import com.kuit.kupage.domain.teamMatch.dto.PortfolioUploadResponse;
import com.kuit.kupage.domain.teamMatch.dto.TeamMatchRequest;
import com.kuit.kupage.domain.teamMatch.dto.TeamMatchResponse;
import com.kuit.kupage.domain.teamMatch.repository.TeamApplicantRepository;
import com.kuit.kupage.domain.teamMatch.repository.TeamRepository;
import com.kuit.kupage.exception.KupageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class TeamMatchServiceTest {

    @Mock
    private MemberService memberService;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamApplicantRepository teamApplicantRepository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private TeamMatchService teamMatchService;

    private Member mockMember;
    private Team mockTeam;
    private TeamApplicant mockApplicant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMember = mock(Member.class);
        mockTeam = mock(Team.class);
        mockApplicant = mock(TeamApplicant.class);

        when(mockApplicant.getId()).thenReturn(1L);
    }

    @Test
    @DisplayName("팀 매칭 신청 성공 시 TeamApplicant가 저장되고 응답이 반환된다")
    void apply_success() {
        // given
        TeamMatchRequest request = new TeamMatchRequest(
                Part.SPRING,
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
        TeamMatchRequest request = mock(TeamMatchRequest.class);
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
    @DisplayName("포트폴리오 업로드 성공 시 업로드된 URL이 반환된다")
    void uploadPortfolio_success() {
        // given
        MultipartFile mockFile = mock(MultipartFile.class);
        given(s3Service.uploadPortfolio(mockFile))
                .willReturn("https://files.kuitee.p-e.kr/portfolio/test.docx");

        // when
        PortfolioUploadResponse response = teamMatchService.uploadPortfolio(mockFile);

        // then
        assertThat(response).isNotNull();
        assertThat(response.portfolioUrl()).isEqualTo("https://files.kuitee.p-e.kr/portfolio/test.docx");

        verify(s3Service).uploadPortfolio(mockFile);
    }

}