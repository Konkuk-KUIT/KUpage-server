package com.kuit.kupage.domain.role.dto;

import lombok.Data;
import java.util.List;

@Data
public class DiscordMember {
    private DiscordUser user;
    private String nick;
    private List<String> roles;
    private String joined_at;
    private boolean deaf;
    private boolean mute;

    @Data
    public static class DiscordUser {
        private String id;
        private String username;
        private String discriminator;
        private String avatar;
    }
}