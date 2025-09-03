package com.kuit.kupage.domain.role.controller;

import com.kuit.kupage.common.response.BaseResponse;
import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.domain.oauth.service.DiscordOAuthService;
import com.kuit.kupage.domain.role.dto.DiscordMemberResponse;
import com.kuit.kupage.domain.role.dto.DiscordRoleResponse;
import com.kuit.kupage.domain.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/role")
@RestController
public class RoleController {
    private final DiscordOAuthService discordOAuthService;
    private final RoleService roleService;

    @PostMapping("/sync")
    public BaseResponse<?> syncRoles() {
        // KUIT 서버의 모든 ROLE 조회, 업데이트
        List<DiscordRoleResponse> roleResponses = discordOAuthService.fetchGuildRoles();
        int updateCnt = roleService.batchInsert(roleResponses);
        log.debug("[syncRoles] 새롭게 생성 및 저장된 Role 개수 = {}", updateCnt);

        // 각 member의 ROLE 조회, 업데이트
        List<DiscordMemberResponse> discordMemberResponses = discordOAuthService.fetchGuildMembers();
        int updatedMemberCnt = roleService.syncMemberRoles(discordMemberResponses);
        log.debug("[syncRoles] role이 업데이트 된 회원 수 = {}", updatedMemberCnt);
        return new BaseResponse<>(ResponseCode.SUCCESS);
    }

}
