package com.kuit.kupage.common.oauth.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DiscordTokenResponse {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String scope;
}
