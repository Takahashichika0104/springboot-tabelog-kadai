package com.example.nagoyameshi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http

                                // ★変更
                                .formLogin(login -> login
                                                .loginPage("/login") // カスタムログイン画面
                                                .loginProcessingUrl("/login")
                                                .defaultSuccessUrl("/stores", true)
                                                .permitAll())

                                .logout(logout -> logout
                                                .logoutSuccessUrl("/login"))

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/",
                                                                "/stores",
                                                                "/login",
                                                                "/password-reset/**",
                                                                "/register/**",
                                                                "/css/**",
                                                                "/js/**",
                                                                "/storage/**")
                                                .permitAll()

                                                .requestMatchers("/mypage").authenticated()

                                                .requestMatchers("/member-menu").authenticated()

                                                .requestMatchers("/membership/**").authenticated()

                                                .requestMatchers("/credit-card/**").authenticated()

                                                .requestMatchers("/admin/**").hasRole("ADMIN")

                                                .requestMatchers("/user/**").hasRole("USER")

                                                .requestMatchers("/favorites/**").authenticated()

                                                .anyRequest().authenticated());

                return http.build();
        }

        // パスワードをBCryptでハッシュ化
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

}