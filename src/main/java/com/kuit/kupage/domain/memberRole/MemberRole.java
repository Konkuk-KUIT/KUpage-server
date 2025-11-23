package com.kuit.kupage.domain.memberRole;

import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.role.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Slf4j
@NoArgsConstructor
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"member_discord_id", "role_id"})
        }
)
public class MemberRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(length = 50)
    private String memberDiscordId;

    public MemberRole(String memberDiscordId, Role role) {
        this.memberDiscordId = memberDiscordId;
        this.role = role;
    }

}
