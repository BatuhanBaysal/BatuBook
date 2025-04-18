package com.batubook.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/**").permitAll()
                        .requestMatchers("/api/user-profiles/**").permitAll()
                        .requestMatchers("/api/books/**").permitAll()
                        .requestMatchers("/api/book-interactions/**").permitAll()
                        .requestMatchers("/api/book-sales/**").permitAll()
                        .requestMatchers("/api/messages/**").permitAll()
                        .requestMatchers("/api/reviews/**").permitAll()
                        .requestMatchers("/api/quotes/**").permitAll()
                        .requestMatchers("/api/follows/**").permitAll()
                        .requestMatchers("/api/likes/**").permitAll()
                        .requestMatchers("/api/repost-saves/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}