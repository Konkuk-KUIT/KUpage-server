package com.kuit.kupage.domain.role.controller;

import com.kuit.kupage.common.response.BaseResponse;
import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.domain.memberRole.service.MemberRoleService;
import com.kuit.kupage.domain.oauth.service.DiscordOAuthService;
import com.kuit.kupage.domain.role.dto.DiscordMemberResponse;
import com.kuit.kupage.domain.role.dto.DiscordRoleResponse;
import com.kuit.kupage.domain.role.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Role Sync",
        description = """
                Discord 역할/회원 역할을 KUITee DB와 동기화하는 백엔드 전용 API입니다.
                프론트엔드 화면에서는 사용하지 않고, 운영용/배치용으로만 호출됩니다.
                """
)
public class RoleController {
    private final DiscordOAuthService discordOAuthService;
    private final RoleService roleService;

    @PostMapping("/sync")
    @Operation(
            summary = "Discord 역할/회원 역할 동기화 (백엔드 전용)",
            description = """
                    KUIT Discord 서버의 역할(Role)과 회원별 역할 정보를 Kupage DB와 동기화합니다.
                    - Discord 길드의 전체 Role 목록을 조회하여 `role` 테이블에 생성·업데이트(batchInsert)합니다.
                    - 일반 사용자가 직접 호출하는 용도가 아니라, 관리자/배치 스크립트에서만 사용해야 합니다.
                    """
    )
    public BaseResponse<?> syncRoles() {
        // KUIT 서버의 모든 ROLE 조회, 업데이트
        List<DiscordRoleResponse> roleResponses = discordOAuthService.fetchGuildRoles();
        int updateCnt = roleService.batchInsertRoles(roleResponses);
        log.debug("[syncRoles] 새롭게 생성 및 저장된 Role 개수 = {}", updateCnt);

        // 각 member의 ROLE 조회, 업데이트
        List<DiscordMemberResponse> discordMemberResponses = discordOAuthService.fetchGuildMembers();
        int updateMemberRoleCnt = roleService.batchInsertRoleMember(discordMemberResponses);
        log.debug("[syncRoles] 새롭게 저장된 memberRole 개수 = {}", updateMemberRoleCnt);
        return new BaseResponse<>(ResponseCode.SUCCESS);
    }

}
