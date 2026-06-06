package com.example.anabadabackend.global.global.util;

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

    @Value("${jwt.access-token-validity:1800000}") // 30분
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity:1209600000}") // 14일
    private long refreshTokenValidity;

    private Key key;

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Access Token 생성
     */
    public String createAccessToken(Long id) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(id));
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
     * Refresh Token 생성
     */
    public String createRefreshToken(Long id) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidity);

        String refreshToken = Jwts.builder()
                .setSubject(String.valueOf(id))
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        storeRefreshTokenInRedis(id, refreshToken);

        return refreshToken;
    }

    /**
     * Redis에 Refresh Token 저장
     */
    public void storeRefreshTokenInRedis(Long id, String refreshToken) {
        redisTemplate.opsForValue().set(
                "user:refresh:" + id,
                refreshToken,
                refreshTokenValidity,
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * Redis에 저장된 Refresh Token 조회
     */
    public String getRefreshTokenFromRedis(Long id) {
        return redisTemplate.opsForValue().get("user:refresh:" + id);
    }

    /**
     * 토큰 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 토큰에서 userId 추출
     */
    public Long getUserId(String token) {
        String subject = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        return Long.parseLong(subject);
    }

    /**
     * Refresh Token 삭제
     */
    public void deleteRefreshTokenFromRedis(Long id) {
        redisTemplate.delete("user:refresh:" + id);
    }
}
