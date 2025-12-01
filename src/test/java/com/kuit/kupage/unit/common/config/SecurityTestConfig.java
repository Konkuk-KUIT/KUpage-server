package com.kuit.kupage.unit.common.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class SecurityTestConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // 모든 요청 허용
                .formLogin(AbstractHttpConfigurer::disable) // 로그인 폼 비활성화
                .httpBasic(AbstractHttpConfigurer::disable); // HTTP Basic 비활성화
        return http.build();
    }
}