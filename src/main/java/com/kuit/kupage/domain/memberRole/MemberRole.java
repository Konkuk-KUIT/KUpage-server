package com.kuit.kupage.domain.memberRole;

import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.role.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Slf4j
@NoArgsConstructor
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

    @NotNull
    @Column(length = 50)
    private String memberDiscordId;

    public MemberRole(String memberDiscordId, Role role) {
        this.memberDiscordId = memberDiscordId;
        this.role = role;
    }

}
