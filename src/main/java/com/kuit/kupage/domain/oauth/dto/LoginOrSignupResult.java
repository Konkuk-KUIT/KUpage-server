package com.kuit.kupage.domain.oauth.dto;

import com.kuit.kupage.common.auth.TokenResponse;

import java.util.List;

public record LoginOrSignupResult(Long memberId, List<String> role, TokenResponse tokenResponse) {
}
