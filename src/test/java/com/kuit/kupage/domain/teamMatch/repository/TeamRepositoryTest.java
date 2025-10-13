package com.kuit.kupage.domain.teamMatch.repository;

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
                "외국인 대상 부동산 매칭 서비스",
                "지도 기반의 실시간 매물 탐색, 채팅 문의, 언어별 번역 기능 제공",
                "다국어 지원, 실시간 매물 등록, 중개인 매칭 기능",
                "https://cdn.kupage.com/team1-thumbnail.jpg",
                "안녕하세요, 저희는 외국인 거주 문제를 해결하고자 합니다.",
                "프로젝트 진행 시 가장 어려운 점은?",
                "지역별 법률 차이로 인한 중개 절차 복잡성이었습니다."
        );

        Team saved = teamRepository.save(team);

        Optional<Team> found = teamRepository.findById(saved.getId());
        assertTrue(found.isPresent());

        Team foundTeam = found.get();
        assertEquals("글로방", foundTeam.getServiceName());
        assertEquals("외국인 대상 부동산 매칭 서비스", foundTeam.getTopicSummary());
        assertEquals("https://cdn.kupage.com/team1-thumbnail.jpg", foundTeam.getThumbnailUrl());
    }

    @Test
    @DisplayName("Team을 삭제하면 더 이상 조회되지 않는다")
    void deleteTeam() {
        // given
        Team team = new Team(
                null,
                "글로방",
                "외국인 대상 부동산 매칭 서비스",
                "지도 기반의 실시간 매물 탐색, 채팅 문의, 언어별 번역 기능 제공",
                "다국어 지원, 실시간 매물 등록, 중개인 매칭 기능",
                "https://cdn.kupage.com/team1-thumbnail.jpg",
                "안녕하세요, 저희는 외국인 거주 문제를 해결하고자 합니다.",
                "프로젝트 진행 시 가장 어려운 점은?",
                "지역별 법률 차이로 인한 중개 절차 복잡성이었습니다."
        );

        teamRepository.save(team);

        // when
        teamRepository.delete(team);
        Optional<Team> found = teamRepository.findById(team.getId());

        // then
        assertThat(found).isEmpty();
    }
}