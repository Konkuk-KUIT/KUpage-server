package com.kuit.kupage.domain.oauth.dto;

import com.kuit.kupage.common.auth.TokenResponse;

public record LoginOrSignupResult(Long memberId, TokenResponse tokenResponse) {
}
