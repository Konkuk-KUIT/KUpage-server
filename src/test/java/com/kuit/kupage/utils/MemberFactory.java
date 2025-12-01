package com.kuit.kupage.utils;

import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MemberFactory {

    @Autowired
    MemberRepository memberRepository;

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Member with id " + id + " not found. Did you run data_init.sql?"));
    }

    public Member getCurrentWebMember() {
        // 6th Web 부원: member_id = 8 (discord_1008)
        return getMember(8L);
    }
    public Member getCurrentServerMember() {
        // 6th Server 부원: member_id = 10 (discord_1010)
        return getMember(10L);
    }
    public Member getCurrentAndroidMember() {
        // 6th Android 부원: member_id = 9 (discord_1009)
        return getMember(9L);
    }
    public Member getCurrentWebTutor() {
        // 6th Web 튜터: member_id = 5 (discord_1005)
        return getMember(5L);
    }
    public Member getCurrentServerTutor() {
        // 6th Server 튜터: member_id = 4 (discord_1004)
        return getMember(4L);
    }
    public Member getCurrentAndroidTutor() {
        // 6th Android 튜터: member_id = 6 (discord_1006)
        return getMember(6L);
    }
    public Member getCurrentAdmin() {
        // 6th 부회장 / 운영진: member_id = 1 (discord_1001)
        return getMember(1L);
    }

    public Member getNotCurrentMember() {
        // 5th 운영진 역할도 가진 멤버: member_id = 1 (discord_1001)
        return getMember(1L);
    }
}
