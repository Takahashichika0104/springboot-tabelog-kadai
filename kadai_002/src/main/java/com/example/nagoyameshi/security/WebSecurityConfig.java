package com.example.nagoyameshi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http

        // ★変更
        .formLogin(login -> login
                .loginPage("/login")        // カスタムログイン画面
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
        )

        .logout(logout -> logout
                .logoutSuccessUrl("/login")
        )

        .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/",
                        "/login",
                        "/register",
                        "/css/**",
                        "/images/**"
                ).permitAll()
                .anyRequest().authenticated()
        );

        return http.build();
    }

    /*
     ★追加
     パスワード暗号化
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

}