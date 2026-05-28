package com.example.anabadabackend.like.dto;

import com.example.anabadabackend.like.entity.Like;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LikeResponse {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("favorite_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime favoriteTime;

    public static LikeResponse from(Like like) {
        return new LikeResponse(
                like.getUserId(),
                like.getProductId(),
                like.getFavoriteTime()
        );
    }
}
