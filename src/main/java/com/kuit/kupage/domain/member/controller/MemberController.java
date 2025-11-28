package com.kuit.kupage.domain.member.controller;

import com.kuit.kupage.common.auth.AuthMember;
import com.kuit.kupage.common.response.BaseResponse;
import com.kuit.kupage.common.swagger.SwaggerErrorResponse;
import com.kuit.kupage.common.swagger.SwaggerErrorResponses;
import com.kuit.kupage.domain.detail.dto.MyPageResponse;
import com.kuit.kupage.domain.detail.dto.MyPageUpdateRequest;
import com.kuit.kupage.domain.member.service.MemberService;
import com.kuit.kupage.domain.memberRole.service.MemberRoleService;
import com.kuit.kupage.domain.role.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.kuit.kupage.common.response.ResponseCode.SUCCESS;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/members")
@Tag(name = "Member", description = "회원 마이페이지 조회 및 수정 API")
public class MemberController {
    private final MemberService memberService;
    private final MemberRoleService memberRoleService;

    @GetMapping("/me")
    @Operation(summary = "마이페이지 조회 API", description = "현재 로그인한 회원의 마이페이지 정보를 조회합니다.")
    @SwaggerErrorResponses(SwaggerErrorResponse.GET_MY_PAGE)
    public BaseResponse<MyPageResponse> getMyPage(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthMember authMember
    ) {
        List<String> roles = memberRoleService.getAllRolesByMemberId(authMember.getId()).stream()
                .map(Role::getName)
                .toList();
        MyPageResponse response = memberService.getMyInfo(authMember.getId(), roles);
        return new BaseResponse<>(response);
    }

    @PutMapping("/me")
    @Operation(summary = "마이페이지 수정 API", description = "현재 로그인한 회원의 마이페이지 정보를 수정합니다.")
    @SwaggerErrorResponses(SwaggerErrorResponse.UPDATE_MY_PAGE)
    public BaseResponse<?> updateMyPage(
            @Valid @RequestBody MyPageUpdateRequest updateRequest,
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthMember authMember
    ) {
        memberService.updateMyInfo(updateRequest, authMember.getId());
        return new BaseResponse<>(SUCCESS);
    }
}
