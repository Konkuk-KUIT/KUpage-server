package com.kuit.kupage.domain.teamMatch.service;

import com.kuit.kupage.common.constant.ConstantProperties;
import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.member.repository.MemberRepository;
import com.kuit.kupage.domain.teamMatch.ApplicantStatus;
import com.kuit.kupage.domain.teamMatch.Part;
import com.kuit.kupage.domain.teamMatch.Team;
import com.kuit.kupage.domain.teamMatch.dto.TeamMatchRequest;
import com.kuit.kupage.domain.teamMatch.repository.TeamApplicantRepository;
import com.kuit.kupage.domain.teamMatch.repository.TeamRepository;
import com.kuit.kupage.domain.utils.EntityFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class TeamMatchServiceConcurrencyTest {

    @Autowired
    TeamMatchService teamMatchService;

    @Autowired
    TeamApplicantRepository teamApplicantRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @MockitoBean
    ConstantProperties constantProperties;


    @BeforeEach
    void cleanDb() {
        // FK 순서 때문에 자식 → 부모 순으로 삭제
        jdbcTemplate.update("DELETE FROM TEAM_APPLICANT");
        jdbcTemplate.update("DELETE FROM TEAM");
        jdbcTemplate.update("DELETE FROM MEMBER");
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM TEAM_APPLICANT");
        jdbcTemplate.update("DELETE FROM TEAM");
        jdbcTemplate.update("DELETE FROM MEMBER");

        when(constantProperties.getApplicantStatus())
                .thenReturn(ApplicantStatus.ROUND1_APPLYING);

        when(constantProperties.getCurrentBatch())
                .thenReturn(Batch.SIXTH);
    }

    @Test
    @DisplayName("[SUCCESS] 동시에 팀매칭 지원 요청이 와도 최대 2개까지만 저장된다.")
    void maximum_apply_is_two() throws InterruptedException {
        // given
        int threadCount = 5;
        Member member = memberRepository.save(EntityFactory.member());
        List<Team> teams = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            teams.add(teamRepository.save(EntityFactory.team()));
        }
        ApplicantStatus applicantStatus = constantProperties.getApplicantStatus();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        // when
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    TeamMatchRequest applyRequest = new TeamMatchRequest(Part.Server, "지원동기", "https://portfolio-url.com");
                    teamMatchService.apply(member.getId(), teams.get(index).getId(), applyRequest);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();                 // 모든 작업을 동시에 시작
        doneLatch.await();                      // 모든 스레드 종료까지 대기
        executorService.shutdown();

        // then : DB에 실제로 저장된 지원 내역이 최대 1개인지 검증
        Batch currentBatch = constantProperties.getCurrentBatch();
        long savedCount = teamApplicantRepository.countByMemberAndStatusAndBatch(member, applicantStatus, currentBatch);
        Assertions.assertThat(successCount.get()).isBetween(1, 2);
        Assertions.assertThat(savedCount).isBetween(1L, 2L);
    }

    @Test
    @DisplayName("[SUCCESS] 한명의 부원은 한 팀에 중복 지원할 수 없다.")
    void one_team_one_apply() throws InterruptedException {
        // given
        int threadCount = 5;
        Member member = memberRepository.save(EntityFactory.member());
        Team team = teamRepository.save(EntityFactory.team());
        ApplicantStatus applicantStatus = constantProperties.getApplicantStatus();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    TeamMatchRequest applyRequest = new TeamMatchRequest(Part.Server, "지원동기", "https://portfolio-url.com");
                    teamMatchService.apply(member.getId(), team.getId(), applyRequest);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();                 // 모든 작업을 동시에 시작
        doneLatch.await();                      // 모든 스레드 종료까지 대기
        executorService.shutdown();

        // then : 동일 팀에 대해 실제로 저장된 지원 내역이 1개인지 검증
        long savedCount = teamApplicantRepository.countByMemberAndTeamAndStatus(member, team, applicantStatus);
        Assertions.assertThat(successCount.get()).isEqualTo(1);
        Assertions.assertThat(savedCount).isEqualTo(1);
    }
}