/**
 * SQS Producer
 * - Discord Bot에서 생성한 역할 변경 이벤트 payload를 SQS로 발행(SendMessage)합니다.
 * - 네트워크/일시적 AWS 오류에 대비해 간단한 재시도(지수 백오프 + jitter)를 적용합니다.
 * - EC2에 IAM Role(Instance Profile)이 붙어 있다면 Access Key를 코드/환경변수에 넣지 않아도
 *   AWS SDK가 기본 자격증명 체인으로 임시 자격증명을 자동 사용합니다.
 *
 * 필요한 환경변수(.env):
 * - SQS_QUEUE_URL: 발행 대상 SQS Queue URL
 * - LOG_LEVEL
 */
import { SQSClient, SendMessageCommand } from "@aws-sdk/client-sqs";
import pino from "pino";

const log = pino({ level: process.env.LOG_LEVEL || "info" });

// AWS SDK v3 SQS 클라이언트
// - credentials는 지정하지 않으면 Default Credential Provider Chain을 통해
//   EC2 Role/환경변수/로컬 설정 등을 순서대로 탐색해 자격증명을 사용합니다.
const sqs = new SQSClient({ region: "ap-northeast-2" });

// 메시지를 발행할 SQS Queue URL
const QUEUE_URL = process.env.SQS_QUEUE_URL;

// 단순 대기 유틸 (재시도 간격 적용)
function sleep(ms) {
    return new Promise((r) => setTimeout(r, ms));
}

// attempt별 대기 시간 계산 반환
// - attempt가 증가할수록 대기 시간을 증가 + 작은 난수를 더해 동시 재시도(Thundering herd)를 완화
function backoffMs(attempt) {
    const base = 200 * Math.pow(2, attempt);
    const jitter = Math.floor(Math.random() * 100);
    return Math.min(base + jitter, 3000);
}

// 역할 변경 이벤트를 SQS로 발행
// - payload는 JSON으로 직렬화되어 MessageBody로 들어감
// - 실패 시 최대 maxAttempts까지 재시도
export async function publishRoleChangeEvent(payload) {
    if (!QUEUE_URL) throw new Error("SQS_QUEUE_URL is not set");

    const body = JSON.stringify(payload);

    // 재시도 정책 (운영/트래픽에 맞춰 조정 가능)
    const maxAttempts = 5;          // 최대 재시도 횟수
    for (let attempt = 0; attempt < maxAttempts; attempt++) {
        try {
            // SQS 메시지 발행 요청
            // 필요하면 MessageAttributes로 eventType, schemaVersion 등을 추가해
            // 소비자 측 필터링/관측에 활용할 수 있습니다.
            const cmd = new SendMessageCommand({
                QueueUrl: QUEUE_URL,
                MessageBody: body,
                // 필요하면 MessageAttributes로 eventType 등 넣어도 됨
            });

            const res = await sqs.send(cmd);
            log.info({ eventId: payload.eventId, messageId: res.MessageId }, "SQS SendMessage OK");
            return res;
        } catch (err) {
            // 일시적 네트워크 오류/스로틀링 등은 재시도하면 회복되는 경우가 많습니다.
            // 최종 실패 시에는 throw하여 상위에서 에러 로그를 남기고(필요시) 알림/대체 경로를 고려합니다.
            const wait = backoffMs(attempt);        // attempt별 대기 시간 계산
            log.warn(
                { err, attempt: attempt + 1, maxAttempts, waitMs: wait, eventId: payload.eventId },
                "SQS SendMessage failed, retrying"
            );
            if (attempt === maxAttempts - 1) throw err;
            await sleep(wait);
        }
    }
}