package com.kuit.kupage.domain.member;

import com.kuit.kupage.common.auth.AuthTokenResponse;
import com.kuit.kupage.common.oauth.dto.DiscordInfoResponse;
import com.kuit.kupage.common.oauth.dto.DiscordTokenResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@Entity
@Table(name = "member")
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String name;

    private String discordId;

    private String discordLoginId;

    private String profileImage;

    @Embedded
    private AuthToken authToken;

    @Embedded
    private DiscordToken discordToken;

    public Member(DiscordTokenResponse response, DiscordInfoResponse userInfo) {

    }

    public void updateOauthToken(DiscordTokenResponse response) {
        this.discordToken.update(response.getAccessToken(),
                response.getRefreshToken(),
                response.getExpiresIn());
    }

    public void updateAuthToken(AuthTokenResponse authTokenResponse) {
        this.authToken.update(authTokenResponse.getAccessToken(), authTokenResponse.getRefreshToken());
    }
}
