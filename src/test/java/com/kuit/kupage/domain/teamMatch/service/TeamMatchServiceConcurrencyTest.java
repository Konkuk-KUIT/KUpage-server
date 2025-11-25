package com.kuit.kupage.domain.teamMatch.service;

import com.kuit.kupage.common.constant.ConstantProperties;
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

    @MockitoBean
    ConstantProperties constantProperties;

    @BeforeEach
    void setUp() {
        when(constantProperties.getApplicantStatus())
                .thenReturn(ApplicantStatus.ROUND1_APPLYING);
    }

    @Test
    @DisplayName("[SUCCESS] 동시에 팀매칭 지원 요청이 와도 최대 2개까지만 저장된다.")
    void maximum_apply_is_two() throws InterruptedException {
        // given
        int threadCount = 10;
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

        // then
        // DB에 실제로 저장된 지원 내역이 최대 2개인지 검증
        long savedCount = teamApplicantRepository.countByMemberAndBatchAndStatus(member, applicantStatus);
        Assertions.assertThat(successCount.get()).isEqualTo(2);
        Assertions.assertThat(savedCount).isEqualTo(2);
    }

}