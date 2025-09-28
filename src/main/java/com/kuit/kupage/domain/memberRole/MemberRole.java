package com.kuit.kupage.domain.memberRole;

import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.role.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Slf4j
@NoArgsConstructor
public class MemberRole {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    public static MemberRole of(Member member, Role role) {
        MemberRole memberRole = new MemberRole();
        memberRole.member = member;
        memberRole.role = role;
        return memberRole;
    }
}
