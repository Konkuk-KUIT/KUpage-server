package com.kuit.kupage.common.oauth.controller;

import com.kuit.kupage.common.oauth.dto.SignupResponse;
import com.kuit.kupage.common.oauth.service.DiscordOAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<SignupResponse> callback(@RequestParam("code") String code){
        log.info("[callback] 디스코드 인가코드 발급 완료 = {}", code);
        SignupResponse response = discordOAuthService.requestToken(code);
        return ResponseEntity.ok(response);
    }
}
