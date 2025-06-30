package com.kuit.kupage.domain.role.service;

import com.kuit.kupage.domain.role.Role;
import com.kuit.kupage.domain.role.dto.DiscordRoleResponse;
import com.kuit.kupage.domain.role.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public int batchUpdate(List<DiscordRoleResponse> roleResponses){
        List<Long> ids = roleResponses.stream()
                .map(DiscordRoleResponse::getId)
                .map(Long::parseLong)
                .toList();
        Set<Long> existingIds = roleRepository.findAllByDiscordRoleId(ids).stream()
                .map(Role::getId)
                .collect(Collectors.toSet());

        List<Role> newRoles = roleResponses.stream()
                .filter(roleDto -> !existingIds.contains(Long.parseLong(roleDto.getId()))) // 없는 ID만 필터링
                .map(roleDto -> {
                    log.debug("[batchUpdate] map 안 : {}", roleDto);
                    try {
                        return Role.fromString(roleDto); // 이름으로부터 Role 생성
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
}
