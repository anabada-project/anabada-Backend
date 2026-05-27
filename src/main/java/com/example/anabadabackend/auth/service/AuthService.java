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
    private final EmailAuthService emailAuthService;

    /**
     * 회원가입
     */
    @Transactional
    public void signup(SignupRequest request) {

        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAuthException(
                    "이미 사용 중인 이메일입니다.",
                    HttpStatus.CONFLICT
            );
        }

        // 이메일 인증 여부 확인
        emailAuthService.checkVerified(request.getEmail());

        // 비밀번호 암호화
        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .userId(request.getId())
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .gender(request.getGender())
                .specialism(request.getSpecialism())
                .generation(request.getGeneration())
                .build();
        userRepository.save(user);

        // 인증 완료 기록 삭제
        emailAuthService.deleteVerification(request.getEmail());
    }

    /**
     * 로그인
     */
    @Transactional
    public TokenResponse signin(SigninRequest request) {

        User user = userRepository.findByUserId(request.getId())
                .orElseThrow(() ->
                        new EmailAuthException(
                                "아이디 또는 비밀번호가 일치하지 않습니다.",
                                HttpStatus.UNAUTHORIZED
                        ));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new EmailAuthException(
                    "아이디 또는 비밀번호가 일치하지 않습니다.",
                    HttpStatus.UNAUTHORIZED
            );
        }

        String accessToken =
                jwtTokenProvider.createAccessToken(
                        user.getId()
                );

        String refreshToken =
                jwtTokenProvider.createRefreshToken(
                        user.getId()
                );

        jwtTokenProvider.storeRefreshTokenInRedis(
                user.getId(),
                refreshToken
        );

        return new TokenResponse(
                accessToken,
                refreshToken
        );
    }
    /**
     * 로그아웃
     */
    @Transactional
    public void signout(Long id) {
        jwtTokenProvider.deleteRefreshTokenFromRedis(id);
    }
}