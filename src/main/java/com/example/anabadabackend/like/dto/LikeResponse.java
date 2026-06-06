package com.example.anabadabackend.like.dto;

import com.example.anabadabackend.like.entity.Like;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeResponse {

    private Long id;
    private Long userId;
    private Long productId;

    public static LikeResponse from(Like like) {
        return new LikeResponse(
                like.getId(),
                like.getUserId(),
                like.getProductId()
        );
    }
}