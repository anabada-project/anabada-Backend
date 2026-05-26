package com.example.anabadabackend.auth.service;

import com.example.anabadabackend.auth.dto.SigninRequest;
import com.example.anabadabackend.auth.dto.SignupRequest;
import com.example.anabadabackend.auth.dto.TokenResponse;
import com.example.anabadabackend.auth.repository.UserRepository;
import com.example.anabadabackend.entity.User;
import com.example.anabadabackend.global.exception.EmailAuthException;
import com.example.anabadabackend.global.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailAuthService emailAuthService; // 정상적으로 이메일 인증 서비스 주입 받음

    /**
     * 회원가입 비즈니스 로직
     */
    @Transactional
    public void signup(SignupRequest request) {
        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAuthException("이미 가입된 이메일입니다.", HttpStatus.BAD_REQUEST);
        }

        // 2. 🟢 EmailAuthService에 정의된 정확한 메서드명인 checkVerified를 호출합니다.
        emailAuthService.checkVerified(request.getEmail());

        // 3. 비밀번호 암호화 (BCrypt)
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 4. 유저 저장
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .build();

        userRepository.save(user);

        // 5. 가입 성공 후 Redis 인증 마크 청소
        emailAuthService.deleteVerification(request.getEmail());
    }

    /**
     * 로그인 비즈니스 로직
     */
    @Transactional
    public TokenResponse signin(SigninRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EmailAuthException("이메일 또는 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new EmailAuthException("이메일 또는 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        // ⚠️ 혹시 이 메서드가 JwtTokenProvider에 아직 구현이 안 되어 있다면
        // 빨간 줄 방지를 위해 잠시 주석 처리하고 테스트하셔도 무방합니다.
        jwtTokenProvider.storeRefreshTokenInRedis(user.getEmail(), refreshToken);

        return new TokenResponse(accessToken, refreshToken);
    }

    /**
     * 로그아웃 비즈니스 로직
     */
    @Transactional
    public void signout(String email) {
        // ⚠️ 혹시 이 메서드가 JwtTokenProvider에 아직 구현이 안 되어 있다면 잠시 주석 처리 가능합니다.
        jwtTokenProvider.deleteRefreshTokenFromRedis(email);
    }
}