package com.kuit.kupage.domain.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DiscordTokenResponse(
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") Long expiresIn,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("scope") String scope
) {
}

