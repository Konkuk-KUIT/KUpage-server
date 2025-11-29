package com.kuit.kupage.domain.member.repository;

import com.kuit.kupage.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByDiscordId(String discordId);

    List<Member> findAllByDiscordIdIn(List<String> discordIds);

    @Query("select m from Member m left join fetch m.detail where m.id = :memberId")
    Optional<Member> findByIdWithDetail(@Param("memberId") Long memberId);
}
