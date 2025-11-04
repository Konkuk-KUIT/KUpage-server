package com.kuit.kupage.domain.memberRole.service;

import com.kuit.kupage.domain.memberRole.MemberRole;
import com.kuit.kupage.domain.memberRole.repository.MemberRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberRoleService {

    private final MemberRoleRepository memberRoleRepository;

    public List<MemberRole> getMemberRolesByMemberId(Long memberId) {
       return memberRoleRepository.findByMemberId(memberId);
    }
}
