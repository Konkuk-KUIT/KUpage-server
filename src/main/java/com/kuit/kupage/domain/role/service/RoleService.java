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

    public int batchUpdate(List<DiscordRoleResponse> roleResponses) {
        List<String> ids = roleResponses.stream()
                .map(DiscordRoleResponse::getId)
                .toList();
        Set<String> existingIds = roleRepository.findAllByDiscordRoleId(ids).stream()
                .map(Role::getDiscordRoleId)
                .collect(Collectors.toSet());

        List<Role> newRoles = roleResponses.stream()
                .filter(roleDto -> !existingIds.contains(roleDto.getId())) // 없는 ID만 필터링
                .map(roleDto -> {
                    try {
                        return Role.fromString(roleDto);
                    } catch (IllegalArgumentException e) {
                        log.error("[batchUpdate] 역할 이름을 Role로 바꾸는 중 오류 발생 : {}", roleDto.getName());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
        List<Role> saved = roleRepository.saveAll(newRoles);
        log.info("[batchUpdate] 새로 저장된 role들 : {}", saved);
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


        // 2. 각 멤버를 순회하며 다음의 로직 수행
        for (DiscordMemberResponse memberResponse : discordMemberResponses) {
            log.debug("[syncMemberRoles] 사용자 discordId = {}, username = {}",
                    memberResponse.getUser().getId(), memberResponse.getUser().getUsername());

            Optional<Member> optionalMember = memberRepository.findByDiscordId(memberResponse.getUser().getId());
            if (optionalMember.isEmpty()) {
                log.error("[syncMemberRoles] KUIT discord 서버에 가입하지 않은 사용자입니다. discordId = {}, username = {}",
                        memberResponse.getUser().getId(), memberResponse.getUser().getUsername());
                continue;
            }
            Member member = optionalMember.get();
            List<Role> newRoles = memberResponse.getRoles().stream()
                    .map(rolesByDiscordId::get)
                    .filter(Objects::nonNull)
                    .toList();
            List<MemberRole> oldRoles = member.getMemberRoles();
            if (hasRolesChanged(oldRoles, newRoles)){
                member.replaceRoles(newRoles);
            }
        }
        return 0;
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
