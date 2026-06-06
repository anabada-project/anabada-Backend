package com.example.anabadabackend.auth.auth.service;

import com.example.anabadabackend.auth.dto.*;
import com.example.anabadabackend.auth.repository.*;
import com.example.anabadabackend.auth.service.EmailAuthService;
import com.example.anabadabackend.entity.User;
import com.example.anabadabackend.global.exception.EmailAuthException;
import com.example.anabadabackend.global.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailAuthService emailAuthService;
    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender mailSender;

    private static final String PASSWORD_CODE_PREFIX = "email:password:";
    private static final String PASSWORD_VERIFIED_PREFIX = "email:password:verified:";

    /**
     * 회원가입
     */
    @Transactional
    public void signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAuthException("이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT);
        }

        emailAuthService.checkVerified(request.getEmail());

        User user = new User(
                request.getId(),
                request.getEmail(),
                request.getName(),
                passwordEncoder.encode(request.getPassword()),
                request.getGender(),
                request.getSpecialism(),
                request.getGeneration()
        );
        userRepository.save(user);

        emailAuthService.deleteVerification(request.getEmail());
    }

    /**
     * 로그인
     */
    @Transactional
    public TokenResponse signin(SigninRequest request) {
        User user = userRepository.findByUserId(request.getId())
                .orElseThrow(() -> new EmailAuthException(
                        "아이디 또는 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new EmailAuthException(
                    "아이디 또는 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        jwtTokenProvider.storeRefreshTokenInRedis(user.getId(), refreshToken);

        return new TokenResponse(accessToken, refreshToken);
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void signout(Long id) {
        jwtTokenProvider.deleteRefreshTokenFromRedis(id);
    }

    /**
     * Access Token 재발급 (RTR 방식)
     */
    @Transactional
    public TokenResponse reissue(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new EmailAuthException("유효하지 않은 RefreshToken입니다.", HttpStatus.UNAUTHORIZED);
        }

        Long userId = jwtTokenProvider.getUserId(refreshToken);

        String storedToken = jwtTokenProvider.getRefreshTokenFromRedis(userId);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new EmailAuthException("RefreshToken이 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(userId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);
        jwtTokenProvider.storeRefreshTokenInRedis(userId, newRefreshToken);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    // ── 비밀번호 재설정 ────────────────────────────────────────────

    /**
     * 비밀번호 재설정 인증코드 발송
     */
    @Transactional
    public void sendPasswordResetCode(String email) {

        // 가입된 이메일인지 확인
        userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailAuthException(
                        "가입되지 않은 이메일입니다.", HttpStatus.NOT_FOUND));

        // 6자리 랜덤 코드 생성
        String code = String.format("%06d", (int) (Math.random() * 1000000));

        // Redis 저장 (TTL 5분)
        redisTemplate.opsForValue().set(
                PASSWORD_CODE_PREFIX + email,
                code,
                5,
                TimeUnit.MINUTES
        );

        // 이메일 발송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[아나바다] 비밀번호 재설정 인증코드");
        message.setText("비밀번호 재설정 인증코드: " + code + "\n\n5분 이내에 입력해주세요.");
        mailSender.send(message);
    }

    /**
     * 비밀번호 재설정 인증코드 검증
     */
    public void verifyPasswordResetCode(String email, String code) {

        String storedCode = redisTemplate.opsForValue()
                .get(PASSWORD_CODE_PREFIX + email);

        if (storedCode == null) {
            throw new EmailAuthException(
                    "인증코드가 만료되었습니다. 다시 요청해주세요.", HttpStatus.BAD_REQUEST);
        }

        if (!storedCode.equals(code)) {
            throw new EmailAuthException(
                    "인증코드가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // 인증코드 삭제
        redisTemplate.delete(PASSWORD_CODE_PREFIX + email);

        // 인증완료 표시 (TTL 10분)
        redisTemplate.opsForValue().set(
                PASSWORD_VERIFIED_PREFIX + email,
                "true",
                10,
                TimeUnit.MINUTES
        );
    }

    /**
     * 비밀번호 재설정
     */
    @Transactional
    public void resetPassword(PasswordResetRequest request) {

        // 인증완료 여부 확인
        String verified = redisTemplate.opsForValue()
                .get(PASSWORD_VERIFIED_PREFIX + request.getEmail());

        if (verified == null) {
            throw new EmailAuthException(
                    "이메일 인증이 필요합니다.", HttpStatus.BAD_REQUEST);
        }

        // 새 비밀번호 == 확인 비밀번호 일치 여부 확인
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new EmailAuthException(
                    "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // DB에 새 비밀번호 저장
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EmailAuthException(
                        "가입되지 않은 이메일입니다.", HttpStatus.NOT_FOUND));

        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));

        // 인증완료 키 삭제
        redisTemplate.delete(PASSWORD_VERIFIED_PREFIX + request.getEmail());
    }
}
