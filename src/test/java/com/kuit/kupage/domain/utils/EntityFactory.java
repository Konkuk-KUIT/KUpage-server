package com.kuit.kupage.domain.utils;

import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.project.entity.AppType;
import com.kuit.kupage.domain.teamMatch.ApplicantStatus;
import com.kuit.kupage.domain.teamMatch.Part;
import com.kuit.kupage.domain.teamMatch.Team;
import com.kuit.kupage.domain.teamMatch.TeamApplicant;

import java.util.ArrayList;

public class EntityFactory {
    public static Member member() {
        return Member.builder()
                .name("테스트 유저")
                .discordId("1234567890")
                .discordLoginId("test_user")
                .profileImage("https://example.com/profile.png")
                .authToken(null)
                .discordToken(null)
                .build();
    }

    public static Member memberWithDiscord(String discordId, String loginId) {
        return Member.builder()
                .name("테스트 유저")
                .discordId(discordId)
                .discordLoginId(loginId)
                .profileImage("https://example.com/profile.png")
                .build();
    }

    public static Team team() {
        return new Team(
                null,
                "글로방",
                AppType.Web,
                "외국인 대상 부동산 매칭 서비스",
                "https://cdn.kupage.com/team1-thumbnail.jpg",
                "https://cdn.kupage.com/team1-intro.pdf",
                "지도 기반의 실시간 매물 탐색, 채팅 문의, 언어별 번역 기능 제공",
                "이런 개발자분이 오시면 좋겠습니다!",
                3L,
                "박민수",
                Batch.SIXTH,
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    public static TeamApplicant teamApplicant(Member member, Team team) {
        return new TeamApplicant(
                null,
                Part.Server,
                "테스트 지원 동기입니다.",
                "https://example.com/portfolio",
                ApplicantStatus.ROUND1_APPLYING,
                member,
                team
        );
    }
}
