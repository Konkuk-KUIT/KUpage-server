package com.kuit.kupage.domain.role.service;

import com.kuit.kupage.domain.role.dto.DiscordMemberResponse;
import com.kuit.kupage.domain.role.dto.DiscordRoleResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Slf4j
public class CsvExporter {
    public static void createRolesToCsv(List<DiscordRoleResponse> roleResponses) {
        String fileName = "discord_roles.csv";
        try (PrintWriter writer = new PrintWriter(fileName)) {
            writer.println("id,name,position,managed,mentionable");
            for (DiscordRoleResponse role : roleResponses) {
                writer.printf("%s,%s,%d,%b,%b%n",
                        role.getId(),
                        escape(role.getName()),
                        role.getPosition(),
                        role.isManaged(),
                        role.isMentionable()
                );
            }
            writer.flush();
            log.info("[createCsv] CSV 파일이 성공적으로 생성되었습니다: {}", fileName);
        } catch (IOException e) {
            log.error("[createCsv] CSV 파일 생성 중 오류 발생", e);
        }
    }

    public static void exportMembersToCsv(List<DiscordMemberResponse> members) {
        String fileName = "discord_members.csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            // CSV 헤더
            writer.println("user_id,username,discriminator,avatar,nick,roles,joined_at,bot");

            for (DiscordMemberResponse member : members) {
                DiscordMemberResponse.DiscordUser user = member.getUser();

                String rolesJoined = String.join(";", member.getRoles());
                String nick = escape(member.getNick());
                String avatar = member.getUser().getAvatar() != null ? user.getAvatar() : "";

                writer.printf("%s,%s,%s,%s,%s,%s,%b%n",
                        user.getId(),
                        escape(user.getUsername()),
                        avatar,
                        nick,
                        escape(rolesJoined),
                        member.getJoined_at(),
                        user.isBot()
                );
            }

            System.out.println("CSV 파일 생성 완료: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String escape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
