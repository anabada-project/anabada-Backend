package com.example.anabadabackend.like.controller;

import com.example.anabadabackend.global.response.ApiResponse;
import com.example.anabadabackend.like.dto.LikeResponse;
import com.example.anabadabackend.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{product_id}")
    public ResponseEntity<ApiResponse<List<LikeResponse>>> toggleProductLike(
            @PathVariable("product_id") Long productId
    ) {
        Long mockUserId = 1L;

        List<LikeResponse> responseList = likeService.toggleLike(mockUserId, productId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Like status updated.", responseList));
    }
}
