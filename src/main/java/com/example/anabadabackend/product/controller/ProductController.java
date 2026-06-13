package com.example.anabadabackend.product.controller;

import com.example.anabadabackend.global.dto.PresignedUrlRequest;
import com.example.anabadabackend.global.dto.PresignedUrlResponse;
import com.example.anabadabackend.global.response.ApiResponse;
import com.example.anabadabackend.global.service.*;
import com.example.anabadabackend.product.dto.ProductCreateRequest;
import com.example.anabadabackend.product.dto.ProductResponse;
import com.example.anabadabackend.product.dto.ProductSearchCondition;
import com.example.anabadabackend.product.dto.ProductUpdateRequest;
import com.example.anabadabackend.product.service.ProductService;
import com.example.anabadabackend.product.service.RecentProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final RecentProductService recentProductService;
    private final S3Uploader s3Uploader;  // 추가



    @PostMapping("/presigned-urls")
    public ResponseEntity<ApiResponse<List<PresignedUrlResponse>>> getPresignedUrls(
            @AuthenticationPrincipal Long userId,
            @RequestBody PresignedUrlRequest request
    ) {
        List<PresignedUrlResponse> urls = s3Uploader.generatePresignedUrls(
                request.getFilenames(),
                request.getContentTypes()
        );
        return ResponseEntity.ok(ApiResponse.ok("Presigned URL 발급 성공", urls));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ProductCreateRequest request
    ) {
        ProductResponse response = productService.create(userId, request);
        return ResponseEntity.ok(ApiResponse.ok("게시글이 등록되었습니다.", response));
    }



    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<?>> getRecentProducts(
            @AuthenticationPrincipal Long userId
    ) {
        List<ProductResponse> result = recentProductService.getRecentProducts(userId);

        if (result.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.ok("최근 본 상품이 없습니다.", null));
        }

        return ResponseEntity.ok(ApiResponse.ok("최근 본 상품 조회 성공", result));
    }



    @GetMapping("/{product_id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(
            @AuthenticationPrincipal Long userId,
            @PathVariable("product_id") Long productId
    ) {
        ProductResponse response = productService.getProduct(userId, productId);
        return ResponseEntity.ok(ApiResponse.ok("게시글 조회 성공", response));
    }



    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProducts(
            @ModelAttribute ProductSearchCondition condition,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProductResponse> response = productService.getProducts(condition, page, size);
        return ResponseEntity.ok(ApiResponse.ok("게시글 목록 조회 성공", response));
    }


    // ── 게시글 수정 ──────────────────────────────────────────
    @PutMapping("/{product_id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @AuthenticationPrincipal Long userId,
            @PathVariable("product_id") Long productId,
            @Valid @RequestBody ProductUpdateRequest request
    ) {
        ProductResponse response = productService.update(userId, productId, request);
        return ResponseEntity.ok(ApiResponse.ok("게시글이 수정되었습니다.", response));
    }


    // ── 게시글 삭제 ──────────────────────────────────────────
    @DeleteMapping("/{product_id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable("product_id") Long productId
    ) {
        productService.delete(userId, productId);
        return ResponseEntity.ok(ApiResponse.ok("게시글이 삭제되었습니다."));
    }
}