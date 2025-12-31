package com.kuit.kupage.infra.handler;

import com.kuit.kupage.domain.role.service.RoleService;
import com.kuit.kupage.infra.domain.ProcessedEventLog;
import com.kuit.kupage.infra.dto.DiscordRoleChangeEvent;
import com.kuit.kupage.infra.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SQS로부터 수신한 Event를 처리하는 서비스
 * 1. eventId 기반 멱등 처리
 * 2. 우리 서비스 DB에 역할 반영하도록 roleService에 위임
 * 3. 실패 시 예외를 던져 SQS 재시도/DLQ로 가도록 설계
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RoleChangeEventService {

    private final ProcessedEventRepository processedEventRepository;
    private final RoleService roleService;

    @Transactional
    public void process(DiscordRoleChangeEvent event) {
        // 1. 멱등 처리: eventId를 기준으로 이미 처리된 이벤트는 제외
        if (!tryMarkProcessed(event)) {
            log.info("[SQS] 중복 이벤트라 처리하지 않습니다. eventId={}", event.eventId());
            return;
        }

        // 2. 쿠이티 DB에 디스코드 역할 변경 반영
        log.info(
                "[SQS] 디스코드 역할 변경 이벤트 처리 시작. eventId={}, guildId={}, discordUserId={}, added={}, removed={}, occurredAt={}",
                event.eventId(),
                event.guildId(),
                event.discordUserId(),
                event.addedRoleIds() == null ? 0 : event.addedRoleIds().size(),
                event.removedRoleIds() == null ? 0 : event.removedRoleIds().size(),
                event.occurredAt()
        );
        roleService.applyDiscordRoleChangeEvent(event);
        log.info("[SQS] 디스코드 역할 변경 이벤트 처리 완료. eventId={}", event.eventId());
    }

    private boolean tryMarkProcessed(DiscordRoleChangeEvent event) {
        try {
            processedEventRepository.save(ProcessedEventLog.from(event));
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;       // 이미 처리된 이벤트
        }
    }
}
