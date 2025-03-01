package com.kuit.kupage.common.oauth.dto.discordInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ApplicationResponse {
    private String id;
    private String name;
    private String icon;
    private String description;
    private boolean hook;

    @JsonProperty("bot_public")
    private boolean botPublic;

    @JsonProperty("bot_require_code_grant")
    private boolean botRequireCodeGrant;

    @JsonProperty("verify_key")
    private String verifyKey;
}
