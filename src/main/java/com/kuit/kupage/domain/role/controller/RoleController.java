package com.kuit.kupage.domain.role.controller;

import com.kuit.kupage.common.response.BaseResponse;
import com.kuit.kupage.domain.oauth.service.DiscordOAuthService;
import com.kuit.kupage.domain.role.dto.DiscordMemberResponse;
import com.kuit.kupage.domain.role.dto.DiscordRoleResponse;
import com.kuit.kupage.domain.role.service.CsvExporter;
import com.kuit.kupage.domain.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping
    public BaseResponse<?> getRole() {
        List<DiscordRoleResponse> roleResponses = discordOAuthService.fetchGuildRoles();
        int updateCnt = roleService.batchUpdate(roleResponses);
        log.info("[getRole] 새롭게 생성 및 저장된 Role 개수 = {}", updateCnt);

        // TODO 2. member의 ROLE 조회 -> DB 데이터와 달라진 점 있으면 변경하기
        List<DiscordMemberResponse> discordMemberResponses = discordOAuthService.fetchGuildMembers();
        CsvExporter.exportMembersToCsv(discordMemberResponses);

//        return new BaseResponse<>(ResponseCode.SUCCESS);
         return new BaseResponse<>(discordMemberResponses);
    }
}
