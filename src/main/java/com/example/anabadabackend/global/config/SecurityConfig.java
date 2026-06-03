package com.example.anabadabackend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    /**
     * 인증 없이 접근 가능한 경로
     * - 이메일 인증 API (회원가입 전 단계)
     * - Swagger UI
     * - Health check
     */
    private static final String[] PUBLIC_URLS = {
            // 이메일 인증
            "/api/auth/email/send",
            "/api/auth/email/verify",

            // Swagger UI (springdoc)
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",

            // Health check
            "/actuator/health",
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // REST API → CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // REST API → 세션 미사용 (Stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // HTTP Basic 로그인 폼 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                // 인가 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated()
                );

        // TODO: JWT 필터 추가 예정
        // http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
