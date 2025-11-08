package com.kuit.kupage.domain.teamMatch.repository;

import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.project.entity.AppType;
import com.kuit.kupage.domain.teamMatch.Part;
import com.kuit.kupage.domain.teamMatch.Team;
import com.kuit.kupage.domain.teamMatch.TeamApplicant;
import com.kuit.kupage.domain.teamMatch.dto.TeamMatchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TeamApplicantRepositoryTest {

    @Autowired
    private TeamApplicantRepository teamApplicantRepository;

    private TeamMatchRequest baseRequest;
    private Member baseMember;
    private Team baseTeam;

    @BeforeEach
    void setUp() {
        baseRequest = new TeamMatchRequest(
                "이서연",
                "20201234",
                Part.WEB,
                "지원동기 예시",
                "https://portfolio.com/jihun",
                "협업에서 중요한 것은 커뮤니케이션",
                "코드 리뷰를 통해 배우고 싶습니다.");


        baseMember = Member.builder()
                .name("이서연")
                .discordId("discord_1002")
                .discordLoginId("seoyeon#5678")
                .profileImage("https://cdn.discordapp.com/embed/avatars/2.png")
                .build();

        baseTeam = new Team(
                null,
                "글로방",
                "외국인 대상 부동산 매칭 서비스",
                "지도 기반의 실시간 매물 탐색, 채팅 문의, 언어별 번역 기능 제공",
                "다국어 지원, 실시간 매물 등록, 중개인 매칭 기능",
                "https://cdn.kupage.com/team1-thumbnail.jpg",
                "안녕하세요, 저희는 외국인 거주 문제를 해결하고자 합니다.",
                "프로젝트 진행 시 가장 어려운 점은?",
                "지역별 법률 차이로 인한 중개 절차 복잡성이었습니다.",
                1L,
                "이서연",
                AppType.Web,
                Batch.SIXTH,
                null,
                null

        );
    }


    @Test
    @DisplayName("TeamApplicant 엔티티를 저장하고 조회할 수 있다")
    void saveAndFindById() {
        // given
        TeamApplicant applicant = new TeamApplicant(baseRequest, baseMember, baseTeam);

        // when
        TeamApplicant saved = teamApplicantRepository.save(applicant);
        Optional<TeamApplicant> found = teamApplicantRepository.findById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("이서연");
        assertThat(found.get().getAppliedPart()).isEqualTo(Part.WEB);
    }

    @Test
    @DisplayName("TeamApplicant를 삭제하면 더 이상 조회되지 않는다")
    void deleteApplicant() {
        // given
        TeamApplicant applicant = new TeamApplicant(baseRequest, baseMember, baseTeam);
        teamApplicantRepository.save(applicant);

        // when
        teamApplicantRepository.delete(applicant);
        Optional<TeamApplicant> found = teamApplicantRepository.findById(applicant.getId());

        // then
        assertThat(found).isEmpty();
    }
}