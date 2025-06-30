package com.kuit.kupage.domain.role;

import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.common.auth.AuthRole;
import com.kuit.kupage.domain.role.dto.DiscordRoleResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
    @SequenceGenerator(name = "role_seq", sequenceName = "role_seq", allocationSize = 50)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Batch batch;                // 기수

    @Enumerated(EnumType.STRING)
    private Name name;                  // 부원, 튜터, 파트장, 운영진..

    private String part;                // 서버, 안드...
    private String discordRoleId;       // 디스코드에 등록된 role의 id

    public AuthRole getAuthRole() {
        return RoleMapper.getRole(name.name());
    }

    static class RoleMapper {
        private static final Map<String, AuthRole> roleMapper = new HashMap<>();

        static {
            roleMapper.put("부원", AuthRole.MEMBER);
            roleMapper.put("튜터", AuthRole.TUTOR);
            roleMapper.put("운영진", AuthRole.ADMIN);
        }

        private static AuthRole getRole(String name) {
            return roleMapper.get(name);
        }
    }

    public static Role fromString(DiscordRoleResponse roleDto) {
        String[] tokens = roleDto.getName().split(" ");

        if (tokens.length == 2) {
            return from(roleDto.getId(), tokens[0], tokens[1]);
        }
        if (tokens.length == 3) {
            return from(roleDto.getId(), tokens[0], tokens[1], tokens[2]);
        }
        throw new IllegalArgumentException();
    }

    private static Role from(String discordRoleId, String batchStr, String nameStr) {
        Batch batch = Batch.parseBatch(batchStr);
        Name name = Name.parseName(nameStr);

        Role role = new Role();
        role.discordRoleId = discordRoleId;
        role.batch = batch;
        role.name = name;

        return role;
    }

    private static Role from(String discordRoleId, String batchStr, String part, String nameStr) {
        Batch batch = Batch.parseBatch(batchStr);
        Name name = Name.parseName(nameStr);

        Role role = new Role();
        role.discordRoleId = discordRoleId;
        role.batch = batch;
        role.part = part;
        role.name = name;

        return role;
    }
}
