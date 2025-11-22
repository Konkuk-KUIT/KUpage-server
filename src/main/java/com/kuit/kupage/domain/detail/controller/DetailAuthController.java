package com.kuit.kupage.domain.detail.controller;

import com.kuit.kupage.common.auth.AuthMember;
import com.kuit.kupage.common.auth.AuthTokenResponse;
import com.kuit.kupage.common.response.BaseResponse;
import com.kuit.kupage.domain.detail.dto.SignupRequest;
import com.kuit.kupage.domain.detail.service.DetailService;
import com.kuit.kupage.domain.oauth.dto.LoginOrSignupResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.kuit.kupage.common.response.ResponseCode.SUCCESS;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "유저 상세 정보 인증 Controller", description = "유저 상세 정보와 인증 관련 Controller 입니다.")
public class DetailAuthController {

    private final DetailService detailService;

    @PostMapping("/signup")
    @Operation(summary = "최종 회원가입 API", description = "유저의 상세 정보를 등록하고 회원가입을 마칩니다.")
    public BaseResponse<LoginOrSignupResult> signup(
            @Valid @RequestBody SignupRequest signupRequest,
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthMember authMember
    ) {

        LoginOrSignupResult response = detailService.signup(signupRequest, authMember.getId());

        return new BaseResponse<>(SUCCESS, response);
    }

}
