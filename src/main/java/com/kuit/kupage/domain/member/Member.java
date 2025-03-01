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

    @Embedded
    private DiscordToken discordToken;

    public Member(DiscordTokenResponse response) {
        this.discordToken = new DiscordToken(response);
    }
}
