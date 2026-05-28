package com.example.anabadabackend.like.controller;

import com.example.anabadabackend.global.response.ApiResponse;
import com.example.anabadabackend.like.dto.LikeResponse;
import com.example.anabadabackend.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<LikeResponse>>> getUserLikeList(
            @AuthenticationPrincipal Long realUserId
    ) {
        List<LikeResponse> responseList = likeService.getLikeListByUser(realUserId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("User like list retrieved successfully.", responseList));
    }

    @PostMapping("/{product_id}")
    public ResponseEntity<ApiResponse<List<LikeResponse>>> addProductLike(
            @PathVariable("product_id") Long productId,
            @AuthenticationPrincipal Long realUserId
    ) {
        List<LikeResponse> responseList = likeService.addLike(realUserId, productId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Like added successfully.", responseList));
    }

    @DeleteMapping("/{product_id}")
    public ResponseEntity<ApiResponse<List<LikeResponse>>> deleteProductLike(
            @PathVariable("product_id") Long productId,
            @AuthenticationPrincipal Long realUserId
    ) {
        List<LikeResponse> responseList = likeService.deleteLike(realUserId, productId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("Like removed successfully.", responseList));
    }
}