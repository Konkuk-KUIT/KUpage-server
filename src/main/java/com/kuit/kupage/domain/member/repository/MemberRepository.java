package com.kuit.kupage.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kuit.kupage.domain.member.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByDiscordId(String discordId);
}
