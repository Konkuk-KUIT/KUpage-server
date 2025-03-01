package com.kuit.kupage.common.oauth.dto.discordInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DiscordInfoResponse {
    private List<String> scopes;
    private Instant expires;

    @JsonProperty("application")
    private ApplicationResponse applicationResponse;

    @JsonProperty("user")
    private UserResponse userResponse;
}
