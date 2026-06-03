package com.example.anabadabackend.account.controller;

import com.example.anabadabackend.global.response.ApiResponse;
import com.example.anabadabackend.account.dto.AccountResponseDto;
import com.example.anabadabackend.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recent")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * 내 정보 조회 API (GET /api/recent)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<AccountResponseDto>> getMyInfo(
            @AuthenticationPrincipal Long id) {
        AccountResponseDto response = accountService.getMyInfo(id);
        return ResponseEntity.ok(ApiResponse.ok("내 정보 조회 성공", response));
    }
}