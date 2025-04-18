package com.kuit.kupage.domain.member;

import com.kuit.kupage.common.auth.AuthTokenResponse;
import com.kuit.kupage.domain.detail.Detail;
import com.kuit.kupage.domain.oauth.dto.DiscordInfoResponse;
import com.kuit.kupage.domain.oauth.dto.DiscordTokenResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@Entity
@ToString
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detail_id")
    private Detail detail;

    public Member(DiscordTokenResponse response, DiscordInfoResponse userInfo) {
        this.discordToken = new DiscordToken(response.accessToken(),
                response.refreshToken(),
                response.expiresIn());
        this.name = userInfo.getUserResponse().getGlobalName();
        this.discordId = userInfo.getUserResponse().getGlobalName();
        this.discordLoginId = userInfo.getUserResponse().getUsername();
        this.profileImage = createProfileImage(userInfo.getUserResponse());
    }

    private String createProfileImage(DiscordInfoResponse.UserResponse userInfo) {
        String avatar = userInfo.getAvatar();
        String userId = userInfo.getId();
        if (avatar == null || avatar.isBlank()) {
            Integer index = Integer.valueOf(userInfo.getId()) % 6;
            return String.format("https://cdn.discordapp.com/embed/avatars/%d.png", index);
        }
        return String.format("https://cdn.discordapp.com/avatars/%s/%s.png", userId, avatar);
    }

    public void updateOauthToken(DiscordTokenResponse response) {
        this.discordToken = new DiscordToken(response.accessToken(),
                response.refreshToken(),
                response.expiresIn());
    }

    public void updateAuthToken(AuthTokenResponse authTokenResponse) {
        this.authToken = new AuthToken(authTokenResponse.accessToken(),
                authTokenResponse.refreshToken());
    }

    public void updateDetail(Detail detail) {
        this.detail = detail;
    }
}
