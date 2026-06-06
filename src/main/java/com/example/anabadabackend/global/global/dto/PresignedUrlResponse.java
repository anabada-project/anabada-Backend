package com.example.anabadabackend.global.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedUrlResponse {
    private String uploadUrl;  // 플러터가 S3에 직접 PUT할 URL
    private String imageUrl;   // DB에 저장할 최종 이미지 URL
}