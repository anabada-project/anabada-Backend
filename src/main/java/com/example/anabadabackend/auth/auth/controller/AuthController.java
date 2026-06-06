package com.example.anabadabackend.auth.auth.controller;

import com.example.anabadabackend.auth.dto.SigninRequest;
import com.example.anabadabackend.auth.dto.SignupRequest;
import com.example.anabadabackend.auth.dto.TokenResponse;
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
}
