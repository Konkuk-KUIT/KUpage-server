package com.kuit.kupage.infra.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public record DiscordRoleChangeEvent(
        String eventType,
        String eventId,
        LocalDateTime occurredAt,         // ISO-8601 (예: 2025-12-30T13:00:00.000Z)
        String guildId,
        String discordUserId,
        String discordLoginId,
        List<String> addedRoleIds,      // 추가된 역할 ID 목록
        List<String> removedRoleIds,    // 제거된 역할 ID 목록
        String source,
        int schemaVersion
) {

    /**
     * Z/offset이 포함된 ISO-8601(ex. 2025-12-30T13:00:00.000Z, +09:00)도 허용하도록
     * OffsetDateTime으로 파싱 후 LocalDateTime으로 변환
     */
    public static DiscordRoleChangeEvent fromJson(String body, ObjectMapper objectMapper) throws Exception {
        JsonNode root = objectMapper.readTree(body);
        return fromJson(root);
    }

    /**
     * 이미 파싱된 JsonNode에서 이벤트를 생성합니다.
     */
    public static DiscordRoleChangeEvent fromJson(JsonNode root) {
        String eventType = textOrNull(root, "eventType");
        String eventId = textOrNull(root, "eventId");
        LocalDateTime occurredAt = parseOccurredAt(textOrNull(root, "occurredAt"));
        String guildId = textOrNull(root, "guildId");
        String discordUserId = textOrNull(root, "discordUserId");
        String discordLoginId = textOrNull(root, "discordLoginId");
        List<String> addedRoleIds = stringListOrEmpty(root, "addedRoleIds");
        List<String> removedRoleIds = stringListOrEmpty(root, "removedRoleIds");
        String source = textOrNull(root, "source");
        int schemaVersion = intOrDefault(root, "schemaVersion", 1);

        return new DiscordRoleChangeEvent(
                eventType,
                eventId,
                occurredAt,
                guildId,
                discordUserId,
                discordLoginId,
                addedRoleIds,
                removedRoleIds,
                source,
                schemaVersion
        );
    }

    private static LocalDateTime parseOccurredAt(String occurredAt) {
        if (occurredAt == null || occurredAt.isBlank()) return null;

        try {
            // 1) LocalDateTime 형식 시도 (예: 2025-12-30T13:00:00)
            return LocalDateTime.parse(occurredAt);
        } catch (DateTimeParseException ignore) {
            // 2) Z/offset 포함 형식 시도 (예: 2025-12-30T13:00:00.000Z / 2025-12-30T22:00:00+09:00)
            try {
                return OffsetDateTime.parse(occurredAt).toLocalDateTime();
            } catch (DateTimeParseException e) {
                return null;
            }
        }
    }

    private static String textOrNull(JsonNode root, String field) {
        JsonNode node = root.get(field);
        if (node == null || node.isNull()) return null;
        String v = node.asText();
        return (v == null || v.isBlank()) ? null : v;
    }

    private static int intOrDefault(JsonNode root, String field, int defaultValue) {
        JsonNode node = root.get(field);
        if (node == null || node.isNull()) return defaultValue;
        if (node.isInt()) return node.asInt();
        try {
            return Integer.parseInt(node.asText());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static List<String> stringListOrEmpty(JsonNode root, String field) {
        JsonNode arr = root.get(field);
        if (arr == null || !arr.isArray()) return List.of();

        List<String> out = new ArrayList<>();
        for (JsonNode n : arr) {
            if (n == null || n.isNull()) continue;
            String v = n.asText();
            if (v != null && !v.isBlank()) out.add(v);
        }
        return out;
    }
}
