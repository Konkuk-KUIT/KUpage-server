package com.kuit.kupage.common.auth;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenResponse {
    private String accessToken;
    private String refreshToken;
}
