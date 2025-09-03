package com.kuit.kupage.domain.member.repository;

import com.kuit.kupage.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByDiscordId(String discordId);

    List<Member> findAllByDiscordIdIn(List<String> discordIds);
}
