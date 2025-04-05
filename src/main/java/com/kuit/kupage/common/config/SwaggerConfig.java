package com.kuit.kupage.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@OpenAPIDefinition(
        servers = @Server(url = "/", description = "default server url"),
        info = @Info(title = "👑 KUITee API 명세서",
                description = "springdoc을 이용한 swagger API 문서입니다 : https://springdoc.org/",
                contact = @Contact(name = "KUIT", url = "https://github.com/Konkuk-KUIT", email = "https://www.konkuk-kuit.com/"),
                version = "1.0"
        )
)

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().components(new Components().addSecuritySchemes("JWT", new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .description("jwt토큰")
                .in(SecurityScheme.In.HEADER)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name(HttpHeaders.AUTHORIZATION)));
    }

}
