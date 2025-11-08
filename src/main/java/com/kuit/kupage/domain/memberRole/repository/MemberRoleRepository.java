package com.kuit.kupage.domain.memberRole.repository;

import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.domain.memberRole.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRoleRepository extends JpaRepository<MemberRole, Long> {

    Boolean existsByMember_IdAndRole_Batch(Long memberId, Batch batch);

    @Query("select mr from MemberRole mr join fetch mr.role where mr.member.id =:memberId")
    List<MemberRole> findWithRoleByMemberId(@Param("memberId") Long memberId);
}
