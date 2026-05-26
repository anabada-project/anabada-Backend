package com.example.anabadabackend.global.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.secret:dGhpcy1pcy1hLXNhbXBsZS1zZWNyZXQta2V5LWZvci1qd3QtdG9rZW4tcHJvdmlkZXItYW5hYmFkYS1wcm9qZWN0}")
    private String secretKey;

    @Value("${jwt.access-token-validity:1800000}") // 30분 기본값
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity:1209600000}") // 14일 기본값
    private long refreshTokenValidity;

    private Key key;

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidity);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Refresh Token 생성 및 Redis 자동 저장
     */
    public String createRefreshToken(String email) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidity);

        String refreshToken = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // 🟢 기존 규칙 유지: user:refresh:[이메일]
        storeRefreshTokenInRedis(email, refreshToken);

        return refreshToken;
    }

    /**
     * 🟢 [AuthService 호환 추가] Redis에 Refresh Token 저장
     */
    public void storeRefreshTokenInRedis(String email, String refreshToken) {
        redisTemplate.opsForValue().set(
                "user:refresh:" + email,
                refreshToken,
                refreshTokenValidity,
                TimeUnit.MILLISECONDS
        );
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * 기존 삭제 메서드
     */
    public void deleteRefreshToken(String email) {
        redisTemplate.delete("user:refresh:" + email);
    }

    /**
     * 🟢 [AuthService 호환 추가] 로그아웃 시 Redis에서 토큰 삭제
     */
    public void deleteRefreshTokenFromRedis(String email) {
        deleteRefreshToken(email);
    }
}