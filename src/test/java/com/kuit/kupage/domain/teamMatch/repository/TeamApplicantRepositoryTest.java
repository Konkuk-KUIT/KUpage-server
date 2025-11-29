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

import static com.kuit.kupage.domain.teamMatch.ApplicantStatus.ROUND1_APPLYING;
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
                Part.Web,
                "지원동기 예시",
                "https://portfolio.com/jihun");


        baseMember = Member.builder()
                .name("이서연")
                .discordId("discord_1002")
                .discordLoginId("seoyeon#5678")
                .profileImage("https://cdn.discordapp.com/embed/avatars/2.png")
                .build();

        baseTeam = new Team(
                null,
                "글로방",
                AppType.Web,
                "외국인 대상 부동산 매칭 서비스",
                "https://cdn.kupage.com/team1-thumbnail.jpg",
                "https://s3.kupage.com/team1-intro.pdf",
                "지도 기반의 실시간 매물 탐색, 채팅 문의, 언어별 번역 기능 제공",
                "부동산 도메인에 관심이 많은 개발자분과 함께 하고 싶습니다!",
                1L,
                "이서연",
                Batch.SIXTH,
                null,
                null
        );
    }


    @Test
    @DisplayName("TeamApplicant 엔티티를 저장하고 조회할 수 있다")
    void saveAndFindById() {
        // given
        TeamApplicant applicant = new TeamApplicant(baseRequest, baseMember, baseTeam, ROUND1_APPLYING, 1, Batch.SIXTH);

        // when
        TeamApplicant saved = teamApplicantRepository.save(applicant);
        Optional<TeamApplicant> found = teamApplicantRepository.findById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getMember().getName()).isEqualTo("이서연");
        assertThat(found.get().getAppliedPart()).isEqualTo(Part.Web);
    }

    @Test
    @DisplayName("TeamApplicant를 삭제하면 더 이상 조회되지 않는다")
    void deleteApplicant() {
        // given
        TeamApplicant applicant = new TeamApplicant(baseRequest, baseMember, baseTeam, ROUND1_APPLYING, 1, Batch.SIXTH);
        teamApplicantRepository.save(applicant);

        // when
        teamApplicantRepository.delete(applicant);
        Optional<TeamApplicant> found = teamApplicantRepository.findById(applicant.getId());

        // then
        assertThat(found).isEmpty();
    }
}