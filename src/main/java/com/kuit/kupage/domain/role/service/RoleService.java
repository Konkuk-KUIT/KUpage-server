package com.kuit.kupage.domain.role.service;

import com.kuit.kupage.domain.memberRole.MemberRole;
import com.kuit.kupage.domain.memberRole.repository.MemberRoleRepository;
import com.kuit.kupage.domain.role.Role;
import com.kuit.kupage.domain.role.dto.DiscordMemberResponse;
import com.kuit.kupage.domain.role.dto.DiscordRoleResponse;
import com.kuit.kupage.domain.role.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final MemberRoleRepository memberRoleRepository;

    public int batchInsertRoles(List<DiscordRoleResponse> roleResponses) {
        List<String> ids = roleResponses.stream()
                .map(DiscordRoleResponse::getId)
                .toList();
        Set<String> existingIds = roleRepository.findAllByDiscordRoleId(ids).stream()
                .map(Role::getDiscordRoleId)
                .collect(Collectors.toSet());

        List<Role> newRoles = getNewRoles(roleResponses, existingIds);

        int successCount = 0;
        for (Role role : newRoles) {
            try {
                roleRepository.save(role);
                successCount++;
            } catch (DataIntegrityViolationException e) {
                log.error("[batchInsert] Role 저장 중 제약조건 위반 발생. discordRoleId = {}, name = {}, message = {}",
                        role.getDiscordRoleId(), role.getName(), e.getMessage());
            }
        }
        return successCount;
    }

    private List<Role> getNewRoles(List<DiscordRoleResponse> roleResponses, Set<String> existingIds) {
        return roleResponses.stream()
                .filter(roleDto -> !existingIds.contains(roleDto.getId())) // 없는 ID만 필터링
                .map(roleDto -> {
                    try {
                        return new Role(roleDto);
                    } catch (IllegalArgumentException e) {
                        log.error("[batchUpdate] 역할 이름을 Role로 바꾸는 중 오류 발생 : {}", roleDto.getName());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public int batchInsertRoleMember(List<DiscordMemberResponse> discordMemberResponses) {
        if (discordMemberResponses == null || discordMemberResponses.isEmpty()) {
            return 0;
        }

        // 1. DB에 저장되어 있는 모든 roles을 Map<discordRoleId, Role>으로 조회
        Map<String, Role> rolesByDiscordId = roleRepository.findAll().stream()
                .filter(role -> role.getDiscordRoleId() != null)
                .collect(Collectors.toMap(
                        Role::getDiscordRoleId,
                        Function.identity()
                ));

        // 2. 모든 MemberRole 조회 후, (memberDiscordId, roleId) 조합으로 Map 구성
        Map<String, MemberRole> existingMemberRoles = memberRoleRepository.findAll().stream()
                .collect(Collectors.toMap(
                        mr -> createKey(mr.getMemberDiscordId(), mr.getRole().getId()),
                        Function.identity(),
                        (existing, duplicate) -> existing
                ));

        // 3. 각 멤버를 순회하며 DB에 없는 MemberRole만 모아서 일괄 저장
        List<MemberRole> newMemberRoles = new ArrayList<>();

        for (DiscordMemberResponse memberResponse : discordMemberResponses) {
            String memberDiscordId = memberResponse.getUser().getId();
            String username = memberResponse.getUser().getUsername();
            if (memberDiscordId == null || memberDiscordId.isBlank()) {
                continue;
            }

            for (String roleDiscordId : memberResponse.getRoles()) {
                Role role = rolesByDiscordId.get(roleDiscordId);
                if (role == null) {
                    log.error("[batchInsertRoleMember] NOT_FOUND discord role id = {}", roleDiscordId);
                    continue;
                }

                String key = createKey(memberDiscordId, role.getId());
                if (existingMemberRoles.containsKey(key)) {
                    log.debug("[batchInsertRoleMember] 이미 존재하는 memberRole : 사용자 discordId = {}, username = {}, roleName = {}",
                            memberDiscordId, username, role.getName());
                    continue;
                }

                log.info("[batchInsertRoleMember] 새로 추가된 memberRole : 사용자 discordId = {}, username = {}, roleName = {}",
                        memberDiscordId, username, role.getName());
                newMemberRoles.add(new MemberRole(memberDiscordId, role));
            }
        }

        if (newMemberRoles.isEmpty()) {
            return 0;
        }
        return memberRoleRepository.saveAll(newMemberRoles).size();
    }

    private String createKey(String memberDiscordId, Long roleId) {
        return "(" + memberDiscordId + "," + roleId + ")";
    }
}
