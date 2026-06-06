package com.example.anabadabackend.auth.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EmailAuthRedisRepository {

    private static final String KEY_PREFIX = "email:verify:";

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 인증 코드 저장
     * key: email:verify:{email}
     * value: {code}:false  (미인증 상태)
     */
    public void saveCode(String email, String code, long ttlSeconds) {
        String key = buildKey(email);
        String value = code + ":false";
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(ttlSeconds));
    }

    /**
     * 인증 코드 조회
     */
    public Optional<String> getCode(String email) {
        String value = redisTemplate.opsForValue().get(buildKey(email));
        if (value == null) return Optional.empty();
        return Optional.of(value.split(":")[0]);
    }

    /**
     * 인증 완료 상태로 업데이트 (TTL 갱신)
     */
    public void markVerified(String email, long ttlSeconds) {
        String key = buildKey(email);
        String code = getCode(email).orElse("verified");
        redisTemplate.opsForValue().set(key, code + ":true", Duration.ofSeconds(ttlSeconds));
    }

    /**
     * 인증 완료 여부 확인
     */
    public boolean isVerified(String email) {
        String value = redisTemplate.opsForValue().get(buildKey(email));
        if (value == null) return false;
        String[] parts = value.split(":");
        return parts.length >= 2 && "true".equals(parts[1]);
    }

    /**
     * 인증 정보 삭제 (회원가입 완료 후 호출)
     */
    public void delete(String email) {
        redisTemplate.delete(buildKey(email));
    }

    private String buildKey(String email) {
        return KEY_PREFIX + email;
    }
}
