package com.kuit.kupage.common.config;

import com.kuit.kupage.common.auth.JwtTokenService;
import com.kuit.kupage.common.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.kuit.kupage.common.auth.AuthRole.*;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenService jwtTokenService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorizeRequest -> authorizeRequest
                        .requestMatchers("/oauth2/code/discord").permitAll())

                .authorizeHttpRequests(authorizeRequest -> authorizeRequest
                        .requestMatchers("/signup")
                        .hasRole(GUEST.getValue()))

                .authorizeHttpRequests(authorizeRequest -> authorizeRequest.
                        requestMatchers("/")
                        .hasRole(MEMBER.getValue()))

                .authorizeHttpRequests(authorizeRequest -> authorizeRequest.anyRequest().hasRole(
                        ADMIN.getValue()))
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenService), UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}
