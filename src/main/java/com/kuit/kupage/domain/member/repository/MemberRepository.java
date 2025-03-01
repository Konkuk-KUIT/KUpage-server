package com.kuit.kupage.domain.member.repository;

import com.kuit.kupage.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
