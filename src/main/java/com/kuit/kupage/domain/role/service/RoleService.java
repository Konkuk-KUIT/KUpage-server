package com.kuit.kupage.domain.role.service;

import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.member.repository.MemberRepository;
import com.kuit.kupage.domain.memberRole.MemberRole;
import com.kuit.kupage.domain.role.Role;
import com.kuit.kupage.domain.role.dto.DiscordMemberResponse;
import com.kuit.kupage.domain.role.dto.DiscordRoleResponse;
import com.kuit.kupage.domain.role.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RoleService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;

    public int batchInsert(List<DiscordRoleResponse> roleResponses) {
        List<String> ids = roleResponses.stream()
                .map(DiscordRoleResponse::getId)
                .toList();
        Set<String> existingIds = roleRepository.findAllByDiscordRoleId(ids).stream()
                .map(Role::getDiscordRoleId)
                .collect(Collectors.toSet());

        List<Role> newRoles = getNewRoles(roleResponses, existingIds);
        List<Role> saved = roleRepository.saveAll(newRoles);
        return saved.size();
    }

    public int syncMemberRoles(List<DiscordMemberResponse> discordMemberResponses) {
        // 1. DB에 저장되어 있는 모든 roles을 Map<id, Role>으로 조회
        List<String> allRoles = discordMemberResponses.stream()
                .map(DiscordMemberResponse::getRoles)
                .flatMap(Collection::stream)
                .distinct()
                .toList();
        Map<String, Role> rolesByDiscordId = roleRepository.findAllByDiscordRoleId(allRoles).stream()
                .collect(Collectors.toMap(
                        Role::getDiscordRoleId,
                        Function.identity()
                ));

        // 2. Discord 멤버 ID를 한 번에 모아 조회
        List<String> discordIds = discordMemberResponses.stream()
                .map(r -> r.getUser().getId())
                .distinct()
                .toList();
        Map<String, Member> memberByDiscordId = memberRepository.findAllByDiscordIdIn(discordIds).stream()
                .collect(Collectors.toMap(Member::getDiscordId, Function.identity()));


        // 3. 각 멤버를 순회하며 DB와 Discord 데이터 동기화
        int updatedMemberNum = 0;
        for (DiscordMemberResponse memberResponse : discordMemberResponses) {
            log.debug("[syncMemberRoles] 사용자 discordId = {}, username = {}",
                    memberResponse.getUser().getId(), memberResponse.getUser().getUsername());

            // 3-1. Discord 사용자 ID로 DB에서 Member 조회
            Member member = memberByDiscordId.get(memberResponse.getUser().getId());
            if (member == null) {
                log.error("[syncMemberRoles] KUIT discord 서버에 가입하지 않은 사용자입니다. discordId = {}, username = {}",
                        memberResponse.getUser().getId(), memberResponse.getUser().getUsername());
                continue;
            }

            // 3-2. 기존 역할과 새 역할이 다를 경우에만 Member 엔티티의 memberRoles 업데이트 수행
            List<Role> newRoles = memberResponse.getRoles().stream()
                    .map(rolesByDiscordId::get)
                    .filter(Objects::nonNull)
                    .toList();

            List<MemberRole> oldRoles = member.getMemberRoles();
            if (hasRolesChanged(oldRoles, newRoles)){
                member.replaceRoles(newRoles);
            }
            updatedMemberNum += 1;
        }
        return updatedMemberNum;
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

    private boolean hasRolesChanged(List<MemberRole> oldRoles, List<Role> newRoles) {
        Set<Long> currentRoleIds = oldRoles.stream()
                .map(memberRole -> memberRole.getRole().getId())
                .collect(Collectors.toSet());

        Set<Long> newRoleIds = newRoles.stream()
                .map(Role::getId)
                .collect(Collectors.toSet());

        return !currentRoleIds.equals(newRoleIds);
    }
}
