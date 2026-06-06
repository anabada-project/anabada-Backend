package com.example.anabadabackend.auth.controller;

import com.example.anabadabackend.auth.dto.SignupRequest;
import com.example.anabadabackend.auth.dto.*;
import com.example.anabadabackend.auth.service.AuthService;
import com.example.anabadabackend.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입 API (POST /api/auth/signup)
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("회원가입이 완료되었습니다.", null));
    }

    /**
     * 로그인 API (POST /api/auth/signin)
     */
    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<TokenResponse>> signin(@Valid @RequestBody SigninRequest request) {
        TokenResponse response = authService.signin(request);
        return ResponseEntity.ok(ApiResponse.ok("로그인에 성공하였습니다.", response));
    }

    /**
     * 로그아웃 API (POST /api/auth/signout)
     */
    @PostMapping("/signout")
    public ResponseEntity<ApiResponse<Void>> signout(@AuthenticationPrincipal Long id) {
        authService.signout(id);
        return ResponseEntity.ok(ApiResponse.ok("로그아웃이 완료되었습니다.", null));
    }

    /**
     * Access Token 재발급 API (POST /api/auth/reissue)
     */
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(
            @RequestHeader("Authorization") String bearerToken
    ) {
        String refreshToken = bearerToken.replace("Bearer ", "");
        TokenResponse response = authService.reissue(refreshToken);
        return ResponseEntity.ok(ApiResponse.ok("토큰이 재발급되었습니다.", response));
    }

    // ── 비밀번호 재설정 ────────────────────────────────────────────

    /**
     * 비밀번호 재설정 인증코드 발송 (POST /api/auth/password/send)
     */
    @PostMapping("/password/send")
    public ResponseEntity<ApiResponse<Void>> sendPasswordResetCode(
            @Valid @RequestBody PasswordResetSendRequest request
    ) {
        authService.sendPasswordResetCode(request.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("인증코드가 발송되었습니다.", null));
    }

    /**
     * 비밀번호 재설정 인증코드 검증 (POST /api/auth/password/verify)
     */
    @PostMapping("/password/verify")
    public ResponseEntity<ApiResponse<Void>> verifyPasswordResetCode(
            @Valid @RequestBody PasswordResetVerifyRequest request
    ) {
        authService.verifyPasswordResetCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(ApiResponse.ok("인증이 완료되었습니다.", null));
    }

    /**
     * 비밀번호 재설정 (POST /api/auth/password/reset)
     */
    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody PasswordResetRequest request
    ) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.ok("비밀번호가 변경되었습니다.", null));
    }
}
