package com.kuit.kupage.domain.member;

import com.kuit.kupage.common.auth.AuthTokenResponse;
import com.kuit.kupage.common.type.BaseEntity;
import com.kuit.kupage.domain.detail.Detail;
import com.kuit.kupage.domain.memberRole.MemberRole;
import com.kuit.kupage.domain.memberTeam.MemberTeam;
import com.kuit.kupage.domain.oauth.dto.DiscordInfoResponse;
import com.kuit.kupage.domain.oauth.dto.DiscordTokenResponse;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Getter
@Slf4j
@Entity
@Table(name = "member")
@NoArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Column(name = "discord_id", unique = true, nullable = false)
    private String discordId;

    @Column(name = "discord_login_id", unique = true, nullable = false)
    private String discordLoginId;

    private String profileImage;

    // TODO. 리프레시 토큰을 db에 저장할지 레디스에 저장할지?
    @Embedded
    private AuthToken authToken;

    @Embedded
    private DiscordToken discordToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detail_id")
    private Detail detail;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberRole> memberRoles = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberTeam> memberTeams = new ArrayList<>();

    public Member(DiscordTokenResponse response, DiscordInfoResponse userInfo) {
        this.discordToken = new DiscordToken(response.accessToken(),
                response.refreshToken(),
                response.expiresIn());
        this.name = userInfo.getUserResponse().getGlobalName();
        this.discordId = userInfo.getUserResponse().getId();
        this.discordLoginId = userInfo.getUserResponse().getUsername();
        this.profileImage = createProfileImage(userInfo.getUserResponse());
    }

    private String createProfileImage(DiscordInfoResponse.UserResponse userInfo) {
        String avatar = userInfo.getAvatar();
        String userId = userInfo.getId();
        if (avatar == null || avatar.isBlank()) {
            Long index = Long.parseLong(userInfo.getId()) % 6;
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
