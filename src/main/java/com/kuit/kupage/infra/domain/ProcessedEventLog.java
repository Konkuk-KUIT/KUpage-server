package com.kuit.kupage.infra.domain;

import com.kuit.kupage.common.type.BaseEntity;
import com.kuit.kupage.infra.dto.DiscordRoleChangeEvent;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "processed_events",
        uniqueConstraints = @UniqueConstraint(name = "uk_processed_events_event_id", columnNames = "event_id")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProcessedEventLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String eventId;

    private String discordUserId;

    private String discordLoginId;

    private String eventType;

    private LocalDateTime occurredAt;

    public static ProcessedEventLog from(DiscordRoleChangeEvent event) {
        ProcessedEventLog processedEventLog = new ProcessedEventLog();
        processedEventLog.eventId = event.eventId();
        processedEventLog.discordUserId = event.discordUserId();
        processedEventLog.discordLoginId = event.discordLoginId();
        processedEventLog.eventType = event.eventType();
        processedEventLog.occurredAt = event.occurredAt();
        return processedEventLog;
    }
}