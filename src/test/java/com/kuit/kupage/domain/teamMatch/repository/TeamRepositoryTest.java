package com.kuit.kupage.domain.teamMatch.repository;

import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.domain.project.entity.AppType;
import com.kuit.kupage.domain.teamMatch.Team;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
    void saveAndFindTeam() {
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
    void deleteTeam() {
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
}