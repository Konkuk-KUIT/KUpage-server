package com.kuit.kupage.domain.role;

import com.kuit.kupage.common.auth.AuthRole;
import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.domain.role.dto.DiscordRoleResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq_gen")
    @SequenceGenerator(name = "role_seq_gen", sequenceName = "role_seq", allocationSize = 50)
    @Column(name = "role_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Batch batch;                // 기수
    private String name;                  // 부원, 튜터, 파트장, 운영진..
    private String discordRoleId;       // 디스코드에 등록된 role의 id
    private Integer position;

    public Role(DiscordRoleResponse roleDto) {
        this.batch = Batch.parseBatch(roleDto.getName());
        this.name = roleDto.getName();
        this.discordRoleId = roleDto.getId();
        this.position = roleDto.getPosition();
    }

    public AuthRole getAuthRole() {
        return RoleMapper.getRole(name);
    }

    static class RoleMapper {
        private static AuthRole getRole(String rawRoleName) {
            String roleName = rawRoleName.toLowerCase();

            if (roleName.contains("운영진") ||
                    roleName.contains("queens") ||
                    roleName.contains("presidents") ||
                    roleName.contains("chairman") ||
                    roleName.contains("trinity") ||
                    roleName.contains("파트장") ||
                    roleName.contains("강의자")) {
                return AuthRole.ADMIN;
            } else if (roleName.contains("스터디리더") ||
                    roleName.contains("튜터")) {
                return AuthRole.TUTOR;
            } else {
                return AuthRole.MEMBER;
            }
        }
    }
}
