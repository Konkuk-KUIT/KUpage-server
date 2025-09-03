package com.kuit.kupage.domain.role.dto;

import lombok.Data;

@Data
public class DiscordRoleResponse {
    private String id;                  // 디스코드 내에서 부여된 역할 고유의 id
    private String name;                // 역할 이름
    private int position;               // 역할의 순서 또는 계층적 위치 (숫자가 클수록 높은 위치)
    private boolean managed;            // Discord 시스템에서 관리되는 역할인지 여부 (예: 봇이 생성한 역할)
    private boolean mentionable;        // 멘션이 가능한 역할인지 여부 (@역할 사용 가능 여부)
}
