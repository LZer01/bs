package com.example.house.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // 禁用 CSRF
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // ⚠️ 允许所有请求通过（开发阶段）
                );
        return http.build();
    }


}