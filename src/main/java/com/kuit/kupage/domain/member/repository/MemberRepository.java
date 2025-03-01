package com.kuit.kupage.domain.member.repository;

import com.kuit.kupage.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByDiscordId(@Param("discordId")String discordId);
}
