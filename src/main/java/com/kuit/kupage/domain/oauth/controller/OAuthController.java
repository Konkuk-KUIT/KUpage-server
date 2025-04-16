package com.kuit.kupage.domain.oauth.controller;

import com.kuit.kupage.common.auth.TokenResponse;
import com.kuit.kupage.common.response.BaseResponse;
import com.kuit.kupage.domain.oauth.service.DiscordOAuthService;
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
public class OAuthController {

    private final DiscordOAuthService discordOAuthService;

    @GetMapping("/code/discord")
    public BaseResponse<? extends TokenResponse> callback(@RequestParam("code") String code) {
        log.info("[callback] 디스코드 인가코드 발급 완료 = {}", code);
        TokenResponse response = discordOAuthService.requestToken(code);
        return response.toBaseResponse();
    }
}
