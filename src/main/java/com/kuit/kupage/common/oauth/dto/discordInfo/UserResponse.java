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
public class UserResponse {
    private String id;
    private String username;
    private String avatar;
    private String discriminator;

    @JsonProperty("global_name")
    private String globalName;

    @JsonProperty("public_flags")
    private int publicFlags;
}
