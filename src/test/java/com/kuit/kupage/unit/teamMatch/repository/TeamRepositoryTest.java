package com.kuit.kupage.unit.teamMatch.repository;

import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.domain.project.domain.AppType;
import com.kuit.kupage.domain.teamMatch.Team;
import com.kuit.kupage.domain.teamMatch.repository.TeamRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class TeamRepositoryTest {

    @Autowired
    private TeamRepository teamRepository;

    @Test
    @DisplayName("Team 엔티티를 저장하고 조회할 수 있다")
    void save_and_find_team() {
        Team team = new Team(
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

        Team saved = teamRepository.save(team);

        Optional<Team> found = teamRepository.findById(saved.getId());
        assertTrue(found.isPresent());

        Team foundTeam = found.get();
        assertEquals("글로방", foundTeam.getServiceName());
        assertEquals("외국인 대상 부동산 매칭 서비스", foundTeam.getTopicSummary());
        assertEquals("https://cdn.kupage.com/team1-thumbnail.jpg", foundTeam.getImageUrl());
    }

    @Test
    @DisplayName("Team을 삭제하면 더 이상 조회되지 않는다")
    void delete_team() {
        // given
        Team team = new Team(
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

        teamRepository.save(team);

        // when
        teamRepository.delete(team);
        Optional<Team> found = teamRepository.findById(team.getId());

        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("batch로 팀 목록을 조회할 수 있다")
    void find_all_by_batch_should_return_teams_for_given_batch() {
        // given
        Team team1 = create_team("글로방", 1L);
        Team team2 = create_team("쿠페이지", 2L);
        teamRepository.save(team1);
        teamRepository.save(team2);

        // when
        List<Team> result = teamRepository.findAllByBatch(Batch.SIXTH);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Team::getServiceName)
                .containsExactlyInAnyOrder("글로방", "쿠페이지");
    }

    @Test
    @DisplayName("batch로 팀과 팀 지원자를 함께 조회할 수 있다")
    void find_all_with_team_applicant_and_member_by_batch_should_return_teams_for_given_batch() {
        // given
        Team team1 = create_team("글로방", 1L);
        teamRepository.save(team1);

        // when
        List<Team> result = teamRepository.findAllWithTeamApplicantAndMemberByBatch(Batch.SIXTH);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getServiceName()).isEqualTo("글로방");
    }

    @Test
    @DisplayName("teamId로 팀과 팀 지원자를 함께 조회할 수 있다")
    void find_all_with_team_applicant_and_member_by_id_should_return_team_for_given_id() {
        // given
        Team team = create_team("글로방", 1L);
        Team saved = teamRepository.save(team);

        // when
        Optional<Team> found = teamRepository.findAllWithTeamApplicantAndMemberById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getServiceName()).isEqualTo("글로방");
    }

    @Test
    @DisplayName("ownerId와 batch로 팀을 조회할 수 있다")
    void find_by_owner_id_and_batch_should_return_team_for_owner_and_batch() {
        // given
        Team team1 = create_team("글로방", 1L);
        Team team2 = create_team("쿠페이지", 2L);
        teamRepository.save(team1);
        teamRepository.save(team2);

        // when
        Optional<Team> found = teamRepository.findByOwnerIdAndBatch(1L, Batch.SIXTH);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getOwnerId()).isEqualTo(1L);
        assertThat(found.get().getServiceName()).isEqualTo("글로방");
    }

    private Team create_team(String serviceName, Long ownerId) {
        return new Team(
                null,
                serviceName,
                AppType.Web,
                "외국인 대상 부동산 매칭 서비스",
                "https://cdn.kupage.com/" + serviceName + "-thumbnail.jpg",
                "https://s3.kupage.com/" + serviceName + "-intro.pdf",
                "지도 기반의 실시간 매물 탐색, 채팅 문의, 언어별 번역 기능 제공",
                "부동산 도메인에 관심이 많은 개발자분과 함께 하고 싶습니다!",
                ownerId,
                "이서연",
                Batch.SIXTH,
                null,
                null
        );
    }
}