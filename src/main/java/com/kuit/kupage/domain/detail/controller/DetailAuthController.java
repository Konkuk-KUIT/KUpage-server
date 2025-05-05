package com.kuit.kupage.domain.detail.controller;

import com.kuit.kupage.common.auth.AuthMember;
import com.kuit.kupage.common.auth.AuthTokenResponse;
import com.kuit.kupage.domain.detail.dto.SignupRequest;
import com.kuit.kupage.domain.detail.service.DetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class DetailAuthController {

    private final DetailService detailService;

    @PostMapping("/signup")
    public ResponseEntity<AuthTokenResponse> signup(@Valid @RequestBody SignupRequest signupRequest,
                                                    @AuthenticationPrincipal AuthMember authMember) {

        AuthTokenResponse response = detailService.signup(signupRequest, authMember.getId());

        return ResponseEntity.ok(response);
    }

}
