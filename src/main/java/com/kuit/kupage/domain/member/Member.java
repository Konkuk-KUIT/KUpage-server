package com.kuit.kupage.domain.member;

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

    public Member(DiscordTokenResponse response) {
        this.discordToken = new DiscordToken(response);
    }

    public void updateOauthToken(String accessToken, String refreshToken, Long expiresIn) {
        this.discordToken.update(accessToken, refreshToken, expiresIn);
    }

    public void updateAuthToken(String accessToken, String refreshToken) {
        this.authToken.update(accessToken, refreshToken);
    }
}
