package com.kuit.kupage.domain.role;

import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.common.auth.AuthRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Slf4j
@NoArgsConstructor
public class Role {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Batch batch; // 기수
    @Enumerated(EnumType.STRING)
    private Name name; // 부원, 튜터, 파트장, 운영진..
    private String part; // 서버, 안드...

    public AuthRole getAuthRole() {
        return RoleMapper.getRole(name.name());
    }

    static class RoleMapper {
        private static final Map<String, AuthRole> roleMapper = new HashMap<>();

        static {
            roleMapper.put("부원", AuthRole.MEMBER);
            roleMapper.put("튜터", AuthRole.TUTOR);
            roleMapper.put("운영진", AuthRole.MEMBER);
        }

        private static AuthRole getRole(String name) {
            return roleMapper.get(name);
        }
    }
}
