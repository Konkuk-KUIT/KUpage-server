/**
 * Discord Bot (Producer)
 * - Discord Gateway(WebSocket)에서 멤버 업데이트 이벤트를 수신
 * - 역할(Role) 변경을 감지하여 added/removed diff를 계산
 * - 계산된 이벤트를 SQS로 발행(SendMessage)하여 비동기 처리 파이프라인을 시작
 *
 * 필요한 환경변수(.env):
 * - DISCORD_BOT_TOKEN: Discord Bot 토큰
 * - SQS_QUEUE_URL: 발행 대상 SQS Queue URL
 * - LOG_LEVEL
 */

// 역할 변경 감지 + diff 계산
import { Client, GatewayIntentBits, Partials } from "discord.js";
import { v4 as uuidv4 } from "uuid";
import pino from "pino";
import { publishRoleChangeEvent } from "./sqsProducer.js";

const log = pino({ level: process.env.LOG_LEVEL || "info" });

const client = new Client({
    intents: [GatewayIntentBits.Guilds, GatewayIntentBits.GuildMembers],
    partials: [Partials.GuildMember],           // 일부 이벤트에서 member 정보가 partial로 들어올 수 있어 fetch()로 보강
});

// oldMember/newMember의 역할 목록을 비교해 "추가된 역할"과 "삭제된 역할"을 계산
// SQS에는 diff만 보내서 메시지를 가볍게 유지
function diffRoles(oldMember, newMember) {
    const oldSet = new Set(oldMember.roles.cache.keys());
    const newSet = new Set(newMember.roles.cache.keys());

    //@everyone 역할은 diff 계산에서 제외 : @everyone 역할은 role id가 guild id와 동일
    oldSet.delete(oldMember.guild.id);
    newSet.delete(newMember.guild.id);

    const added = [];
    const removed = [];

    for (const r of newSet) if (!oldSet.has(r)) added.push(r);
    for (const r of oldSet) if (!newSet.has(r)) removed.push(r);

    return { added, removed };
}

client.on("ready", () => {
    log.info({ botUser: client.user?.tag }, "Discord bot ready");
});

// KST(Asia/Seoul) 기준으로 "YYYY-MM-DDTHH:mm:ss" 형태의 문자열을 생성
function nowKstLocalDateTimeString() {
    const now = new Date();
    // sv-SE 포맷은 24시간제 "YYYY-MM-DD HH:mm:ss" 형태로 안정적으로 출력되어 가공이 쉽습니다.
    const kst = new Intl.DateTimeFormat("sv-SE", {
        timeZone: "Asia/Seoul",
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
        hour12: false,
    }).format(now);

    return kst.replace(" ", "T");
}


client.on("guildMemberUpdate", async (oldMember, newMember) => {
    try {
        // 이벤트 payload가 불완전하게 들어오는 경우가 있어 필요한 정보를 fetch로 보강
        // 네트워크 호출이므로 실패 가능 → try/catch로 감싸기
        if (oldMember.partial) oldMember = await oldMember.fetch();
        if (newMember.partial) newMember = await newMember.fetch();

        const { added, removed } = diffRoles(oldMember, newMember);
        // 역할 변화가 없으면 SQS로 발행하지 않음
        if (added.length === 0 && removed.length === 0) return;

        // SQS로 발행할 이벤트 메시지
        // - eventId: 중복 발행/재시도 상황에서 소비자가 멱등 처리할 수 있도록 UUID 부여
        const payload = {
            eventType: "DISCORD_ROLE_CHANGED",
            eventId: uuidv4(),
            occurredAt: nowKstLocalDateTimeString(),
            guildId: newMember.guild.id,
            userId: newMember.user.id,
            addedRoleIds: added,
            removedRoleIds: removed,
            source: "discord-bot",
            schemaVersion: 1,
        };

        log.info(
            { eventId: payload.eventId, userId: payload.userId, added, removed },
            "Role change detected"
        );

        await publishRoleChangeEvent(payload);
    } catch (err) {
        log.error({ err }, "Failed handling guildMemberUpdate");
    }
});

// Discord Gateway 로그인 (실패 시 토큰/인텐트/네트워크 설정을 점검하세요)
await client.login(process.env.DISCORD_BOT_TOKEN);