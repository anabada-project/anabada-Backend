package com.example.anabadabackend.account.controller;

import com.example.anabadabackend.account.dto.AccountResponse;
import com.example.anabadabackend.account.service.AccountService;
import com.example.anabadabackend.global.response.ApiResponse;
import com.example.anabadabackend.product.dto.ProductResponse;
import com.example.anabadabackend.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final ProductService productService;

    /**
     * GET /api/account/me
     * 내 정보 조회 (로그인 필수)
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AccountResponse>> getMyInfo(
            @AuthenticationPrincipal Long userId
    ) {
        AccountResponse response = accountService.getMyInfo(userId);
        return ResponseEntity.ok(ApiResponse.ok("내 정보 조회 성공", response));
    }

    /**
     * GET /api/account/posts
     * 내 게시글 목록 조회 (로그인 필수)
     */
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getMyProducts(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProductResponse> response = productService.getMyProducts(userId, page, size);
        return ResponseEntity.ok(ApiResponse.ok("내 게시글 목록 조회 성공", response));
    }
}
