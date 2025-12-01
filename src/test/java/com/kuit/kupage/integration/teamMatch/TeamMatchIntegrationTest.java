//package com.kuit.kupage.integration.teamMatch;
//
//import com.kuit.kupage.common.constant.ConstantProperties;
//import com.kuit.kupage.domain.member.Member;
//import com.kuit.kupage.domain.member.repository.MemberRepository;
//import com.kuit.kupage.domain.teamMatch.ApplicantStatus;
//import com.kuit.kupage.domain.teamMatch.Part;
//import com.kuit.kupage.domain.teamMatch.Team;
//import com.kuit.kupage.domain.teamMatch.TeamApplicant;
//import com.kuit.kupage.domain.teamMatch.dto.TeamMatchRequest;
//import com.kuit.kupage.domain.teamMatch.repository.TeamApplicantRepository;
//import com.kuit.kupage.domain.teamMatch.repository.TeamRepository;
//import com.kuit.kupage.domain.teamMatch.service.TeamMatchService;
//import com.kuit.kupage.exception.KupageException;
//import com.kuit.kupage.utils.DbCleaner;
//import com.kuit.kupage.utils.MemberFactory;
//import io.restassured.RestAssured;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
//@Sql(scripts = "/data_init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//public class TeamMatchIntegrationTest {
//
//    @LocalServerPort
//    int port;
//
//    @Autowired
//    DbCleaner dbCleaner;
//
//    @Autowired
//    ConstantProperties constantProperties;
//
//    @Autowired
//    MemberFactory memberFactory;
//
//    @Autowired
//    MemberRepository memberRepository;
//
//    @Autowired
//    TeamApplicantRepository teamApplicantRepository;
//
//    @Autowired
//    TeamRepository teamRepository;
//
//    @Autowired
//    TeamMatchService teamMatchService;
//
//    @BeforeEach
//    void setUp() {
//        RestAssured.port = port;
//        dbCleaner.truncateAll();
//    }
//
//    private void setApplicantTimeWindow(LocalDateTime firstRoundResultTime, LocalDateTime secondRoundResultTime) {
//        ReflectionTestUtils.setField(constantProperties, "firstRoundResultTime", firstRoundResultTime);
//        ReflectionTestUtils.setField(constantProperties, "secondRoundResultTime", secondRoundResultTime);
//    }
//
//    private Long anyTeamId() {
//        List<Team> teams = teamRepository.findAll();
//        if (teams.isEmpty()) {
//            throw new IllegalStateException("테스트에 사용할 team 데이터가 없습니다. data_init.sql에 team insert를 추가해주세요.");
//        }
//        return teams.get(0).getId();
//    }
//
//    @Test
//    @DisplayName("POST /teams/{teamId}/match - 1차 지원 기간에는 slot=1, status=ROUND1_APPLYING 으로 저장된다")
//    void apply_firstRound_savesSlot1AndRound1Status() {
//        // given: 지금 시점 < firstRoundResultTime → ROUND1_APPLYING 구간
//        LocalDateTime now = LocalDateTime.now();
//        setApplicantTimeWindow(now.plusHours(1), now.plusDays(1));
//
//        Member applicant = memberFactory.getCurrentWebMember();
//        Long teamId = anyTeamId();
//
//        TeamMatchRequest request = new TeamMatchRequest(
//                Part.Web,
//                "1차 지원 동기입니다.",
//                "https://portfolio.example.com/round1"
//        );
//
//        // when
//        teamMatchService.apply(applicant.getId(), teamId, request);
//
//        // then: 1개의 지원만 생성되고, slot=1 + ROUND1_APPLYING 이어야 한다
//        List<TeamApplicant> applicants = teamApplicantRepository.findAll();
//        assertThat(applicants).hasSize(1);
//
//        TeamApplicant saved = applicants.get(0);
//        assertThat(saved.getSlotNo()).isEqualTo(1);
//        assertThat(saved.getStatus()).isEqualTo(ApplicantStatus.ROUND1_APPLYING);
//    }
//
//    @Test
//    @DisplayName("POST /teams/{teamId}/match - 1차 지원 기간에는 동일한 팀에 1번만 지원할 수 있다")
//    void apply_firstRound_onlyOncePerTeam() {
//        // given
//        LocalDateTime now = LocalDateTime.now();
//        setApplicantTimeWindow(now.plusHours(1), now.plusDays(1));
//
//        Member applicant = memberFactory.getCurrentWebMember();
//        Long teamId = anyTeamId();
//
//        TeamMatchRequest request = new TeamMatchRequest(
//                Part.Web,
//                "1차 지원 동기입니다.",
//                "https://portfolio.example.com/round1"
//        );
//
//        // when: 첫 번째 지원은 성공
//        teamMatchService.apply(applicant.getId(), teamId, request);
//
//        // then: 두 번째 동일 팀 재지원 시 비즈니스 예외 발생 + DB에는 여전히 1건만 존재
//        assertThatThrownBy(() -> teamMatchService.apply(applicant.getId(), teamId, request))
//                .isInstanceOf(KupageException.class);
//
//        List<TeamApplicant> applicants = teamApplicantRepository.findAll();
//        assertThat(applicants).hasSize(1);
//    }
//
//    @Test
//    @DisplayName("POST /teams/{teamId}/match - 2차 지원 기간에는 slot=2, status=ROUND2_APPLYING 으로 저장된다")
//    void apply_secondRound_savesSlot2AndRound2Status() {
//        // given: firstRoundResultTime < 지금 < secondRoundResultTime → ROUND2_APPLYING 구간
//        LocalDateTime now = LocalDateTime.now();
//        setApplicantTimeWindow(now.minusHours(1), now.plusHours(1));
//
//        Member applicant = memberFactory.getCurrentWebMember();
//        Long teamId = anyTeamId();
//
//        TeamMatchRequest request = new TeamMatchRequest(
//                Part.Web,
//                "2차 지원 동기입니다.",
//                "https://portfolio.example.com/round2"
//        );
//
//        // when
//        teamMatchService.apply(applicant.getId(), teamId, request);
//
//        // then
//        List<TeamApplicant> applicants = teamApplicantRepository.findAll();
//        assertThat(applicants).hasSize(1);
//
//        TeamApplicant saved = applicants.get(0);
//        assertThat(saved.getSlotNo()).isEqualTo(2);
//        assertThat(saved.getStatus()).isEqualTo(ApplicantStatus.ROUND2_APPLYING);
//    }
//
//    @Test
//    @DisplayName("POST /teams/{teamId}/match - 2차 지원 기간에는 동일한 팀에 1번만 지원할 수 있다")
//    void apply_secondRound_onlyOncePerTeam() {
//        // given
//        LocalDateTime now = LocalDateTime.now();
//        setApplicantTimeWindow(now.minusHours(1), now.plusHours(1));
//
//        Member applicant = memberFactory.getCurrentWebMember();
//        Long teamId = anyTeamId();
//
//        TeamMatchRequest request = new TeamMatchRequest(
//                Part.Web,
//                "2차 지원 동기입니다.",
//                "https://portfolio.example.com/round2"
//        );
//
//        // when
//        teamMatchService.apply(applicant.getId(), teamId, request);
//
//        // then
//        assertThatThrownBy(() -> teamMatchService.apply(applicant.getId(), teamId, request))
//                .isInstanceOf(KupageException.class);
//
//        List<TeamApplicant> applicants = teamApplicantRepository.findAll();
//        assertThat(applicants).hasSize(1);
//    }
//
//    @Test
//    @DisplayName("POST /teams/{teamId}/match - 2차 결과 발표 이후에는 지원이 불가능하다")
//    void apply_afterSecondRound_isNotAllowed() {
//        // given: 지금 > secondRoundResultTime → 지원 기간 아님
//        LocalDateTime now = LocalDateTime.now();
//        setApplicantTimeWindow(now.minusDays(2), now.minusDays(1));
//
//        Member applicant = memberFactory.getCurrentWebMember();
//        Long teamId = anyTeamId();
//
//        TeamMatchRequest request = new TeamMatchRequest(
//                Part.Web,
//                "지연 지원 동기입니다.",
//                "https://portfolio.example.com/late"
//        );
//
//        // when & then: 예외가 발생하고 DB에는 아무 지원도 없어야 한다
//        assertThatThrownBy(() -> teamMatchService.apply(applicant.getId(), teamId, request))
//                .isInstanceOf(KupageException.class);
//
//        List<TeamApplicant> applicants = teamApplicantRepository.findAll();
//        assertThat(applicants).isEmpty();
//    }
//}