package com.kuit.kupage.domain.role.service;

import com.kuit.kupage.domain.memberRole.MemberRole;
import com.kuit.kupage.domain.memberRole.repository.MemberRoleRepository;
import com.kuit.kupage.domain.role.Role;
import com.kuit.kupage.domain.role.dto.DiscordMemberResponse;
import com.kuit.kupage.domain.role.dto.DiscordRoleResponse;
import com.kuit.kupage.domain.role.repository.RoleRepository;
import com.kuit.kupage.infra.dto.DiscordRoleChangeEvent;
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

    public void applyDiscordRoleChangeEvent(DiscordRoleChangeEvent event) {
        final String memberDiscordId = event.discordUserId();
        if (memberDiscordId == null || memberDiscordId.isBlank()) {
            log.warn("[역할변경반영] discordUserId가 비어 있어 처리하지 않습니다. eventId={}", event.eventId());
            return;
        }

        final List<String> addedRoleIds = event.addedRoleIds() == null ? List.of() : event.addedRoleIds();
        final List<String> removedRoleIds = event.removedRoleIds() == null ? List.of() : event.removedRoleIds();

        if (addedRoleIds.isEmpty() && removedRoleIds.isEmpty()) {
            log.info("[역할변경반영] 추가/삭제 역할이 없어 처리하지 않습니다. eventId={}, discordUserId={}", event.eventId(), memberDiscordId);
            return;
        }

        // 1. 이번 이벤트에 포함된 roleId들을 조회해서 Map<discordRoleId, Role> 생성
        Set<String> targetDiscordRoleIds = new HashSet<>();
        targetDiscordRoleIds.addAll(addedRoleIds);
        targetDiscordRoleIds.addAll(removedRoleIds);

        Map<String, Role> rolesByDiscordId = roleRepository.findAllByDiscordRoleId(new ArrayList<>(targetDiscordRoleIds)).stream()
                .filter(r -> r.getDiscordRoleId() != null)
                .collect(Collectors.toMap(Role::getDiscordRoleId, Function.identity(), (a, b) -> a));

        // 2. 현재 사용자(memberDiscordId)의 기존 MemberRole 목록 조회
        List<MemberRole> existingForMember = memberRoleRepository.findAll().stream()
                .filter(mr -> memberDiscordId.equals(mr.getMemberDiscordId()))
                .toList();

        Set<String> existingRoleDiscordIds = existingForMember.stream()
                .map(mr -> mr.getRole() == null ? null : mr.getRole().getDiscordRoleId())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 3. 추가 역할 반영: 없으면 insert
        List<MemberRole> toInsert = new ArrayList<>();
        for (String roleDiscordId : addedRoleIds) {
            Role role = rolesByDiscordId.get(roleDiscordId);
            if (role == null) {
                log.warn("[역할변경반영] DB에 없는 디스코드 역할이라 추가를 건너뜁니다. discordRoleId={}, eventId={}", roleDiscordId, event.eventId());
                continue;
            }
            if (existingRoleDiscordIds.contains(roleDiscordId)) {
                log.debug("[역할변경반영] 이미 부여된 역할입니다. discordUserId={}, discordRoleId={}, roleName={}",
                        memberDiscordId, roleDiscordId, role.getName());
                continue;
            }
            toInsert.add(new MemberRole(memberDiscordId, role));
        }

        if (!toInsert.isEmpty()) {
            try {
                memberRoleRepository.saveAll(toInsert);
                log.info("[역할변경반영] 역할 {}개 추가 완료. eventId={}, discordUserId={}",
                        toInsert.size(), event.eventId(), memberDiscordId);
            } catch (DataIntegrityViolationException e) {
                // 동시성/중복 저장 시도 등으로 제약조건 위반이 날 수 있으므로 로그만 남기고 진행
                log.warn("[역할변경반영] 역할 추가 중 제약조건 위반이 발생했습니다. eventId={}, discordUserId={}, message={}",
                        event.eventId(), memberDiscordId, e.getMessage());
            }
        }

        // 4. 제거 역할 반영: 있으면 delete
        Set<String> removedSet = new HashSet<>(removedRoleIds);
        List<MemberRole> toDelete = existingForMember.stream()
                .filter(mr -> mr.getRole() != null && mr.getRole().getDiscordRoleId() != null)
                .filter(mr -> removedSet.contains(mr.getRole().getDiscordRoleId()))
                .toList();

        if (!toDelete.isEmpty()) {
            memberRoleRepository.deleteAll(toDelete);
            log.info("[역할변경반영] 역할 {}개 제거 완료. eventId={}, discordUserId={}",
                    toDelete.size(), event.eventId(), memberDiscordId);
        }

        log.info("[역할변경반영] 처리 완료. eventId={}, discordUserId={}, 추가={}, 제거={}",
                event.eventId(), memberDiscordId, addedRoleIds.size(), removedRoleIds.size());
    }

    private String createKey(String memberDiscordId, Long roleId) {
        return "(" + memberDiscordId + "," + roleId + ")";
    }

}
