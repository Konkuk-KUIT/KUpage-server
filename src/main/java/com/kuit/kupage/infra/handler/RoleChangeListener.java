package com.kuit.kupage.infra.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.kuit.kupage.exception.KupageException;
import com.kuit.kupage.infra.dto.DiscordRoleChangeEvent;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.kuit.kupage.common.response.ResponseCode.SQS_MESSAGE_HANDLE_FAIL;

@Slf4j
@Service
public class RoleChangeListener {

    private final ObjectMapper objectMapper;
    private final RoleChangeEventService roleChangeEventService;

    public RoleChangeListener(
            ObjectMapper objectMapper,
            RoleChangeEventService roleChangeEventService
    ) {
        this.objectMapper = objectMapper;
        this.roleChangeEventService = roleChangeEventService;
    }

    @SqsListener("${sqs.queue-name}")
    public void handle(String body) {
        try {
            DiscordRoleChangeEvent event = DiscordRoleChangeEvent.fromJson(body, objectMapper);

            if (event.eventId() == null || event.eventId().isBlank()) {
                log.warn("[SQS] eventId가 비어있어 메시지를 무시합니다. body={}", body);
                return;
            }

            log.info(
                    "[SQS] 디스코드 역할 변경 이벤트 수신. eventId={}, guildId={}, discordUserId={}, discordLoginId={}, added={}, removed={}, occurredAt={}",
                    event.eventId(),
                    event.guildId(),
                    event.discordUserId(),
                    event.discordLoginId(),
                    event.addedRoleIds() == null ? 0 : event.addedRoleIds().size(),
                    event.removedRoleIds() == null ? 0 : event.removedRoleIds().size(),
                    event.occurredAt()
            );

            roleChangeEventService.process(event);

        } catch (JsonProcessingException | IllegalArgumentException e) {
            // 파싱/검증 실패 메시지 => 재시도해도 성공할 가능성이 낮음
            log.error("[SQS] 유효하지 않은 메시지라 재시도 없이 폐기. body={}", body, e);
            return;
        } catch (Exception e) {
            // 처리 실패(일시 장애/DB lock/네트워크 등) => 재시도하면 성공
            // 메시지는 visibility timeout 후 재전달, 설정된 maxReceiveCount를 넘으면 DLQ로 이동
            log.error("[SQS] 메시지 처리 실패로 재전달을 통해 재시도. body={}", body, e);
            throw new KupageException(SQS_MESSAGE_HANDLE_FAIL);
        }
    }
}
