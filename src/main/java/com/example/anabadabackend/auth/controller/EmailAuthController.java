package com.example.anabadabackend.auth.controller;

import com.example.anabadabackend.auth.dto.EmailSendRequest;
import com.example.anabadabackend.auth.dto.EmailVerifyRequest;
import com.example.anabadabackend.auth.service.EmailAuthService;
import com.example.anabadabackend.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/email")
@RequiredArgsConstructor
public class EmailAuthController {

    private final EmailAuthService emailAuthService;

    /**
     * POST /api/auth/email/send
     * 인증 이메일 발송
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendEmail(
            @Valid @RequestBody EmailSendRequest request
    ) {
        emailAuthService.sendVerificationEmail(request.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("인증 코드가 발송되었습니다."));
    }

    /**
     * POST /api/auth/email/verify
     * 인증 코드 검증
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @Valid @RequestBody EmailVerifyRequest request
    ) {
        emailAuthService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(ApiResponse.ok("이메일 인증이 완료되었습니다."));
    }
}
