package com.kuit.kupage.domain.role.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DiscordRoleChangeEvent(
        String eventType,
        String eventId,
        LocalDateTime occurredAt,         // ISO-8601 (예: 2025-12-30T13:00:00.000Z)
        String guildId,
        String userId,                  // Discord User ID
        List<String> addedRoleIds,      // 추가된 역할 ID 목록
        List<String> removedRoleIds,    // 제거된 역할 ID 목록
        String source,
        int schemaVersion
) {
}
