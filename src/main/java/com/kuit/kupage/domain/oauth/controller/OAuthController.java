package com.kuit.kupage.domain.oauth.controller;

import com.kuit.kupage.common.auth.TokenResponse;
import com.kuit.kupage.common.response.BaseResponse;
import com.kuit.kupage.domain.oauth.service.DiscordOAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/oauth2")
@RestController
@Tag(name = "OAuth 컨트롤러", description = "OAuth 관련 Controller 입니다.")
public class OAuthController {

    private final DiscordOAuthService discordOAuthService;

    @GetMapping("/code/discord")
    @Operation(summary = "디스코드 로그인/회원가입 API", description = "인가코드를 제공하고 로그인/회원가입을 진행합니다.")
    public BaseResponse<? extends TokenResponse> callback(@RequestParam("code") String code) {
        log.info("[callback] 디스코드 인가코드 발급 완료 = {}", code);
        TokenResponse response = discordOAuthService.requestToken(code);
        return response.toBaseResponse();
    }
}
