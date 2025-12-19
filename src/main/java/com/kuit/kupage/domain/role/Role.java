package com.kuit.kupage.domain.role;

import com.kuit.kupage.common.type.BaseEntity;
import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.domain.role.dto.DiscordRoleResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "roles")
@Getter
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Batch batch;                // 기수
    private String name;                // 부원, 튜터, 파트장, 운영진..
    private String discordRoleId;       // 디스코드에 등록된 role의 id
    private Integer position;           // 디스코드에 등록된 role의 position (숫자가 클수록 권한이 많음)

    public Role(DiscordRoleResponse roleDto) {
        this.batch = Batch.parseBatch(roleDto.getName());
        this.name = roleDto.getName();
        this.discordRoleId = roleDto.getId();
        this.position = roleDto.getPosition();
    }

    public Role(Batch batch, String name, String discordRoleId, Integer position) {
        this.batch = batch;
        this.name = name;
        this.discordRoleId = discordRoleId;
        this.position = position;
    }
}
