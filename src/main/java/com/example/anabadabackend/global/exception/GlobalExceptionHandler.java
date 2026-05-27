package com.example.anabadabackend.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // 🟢 프로젝트 전체에서 터지는 에러를 가로채는 통역사 역할을 합니다.
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAuthException.class)
    public ResponseEntity<Map<String, Object>> handleEmailAuthException(EmailAuthException e) {
        Map<String, Object> response = new HashMap<>();

        // 포스트맨에 보여줄 예쁜 구조 짜기
        response.put("status", e.getStatus().value());     // 예: 400 또는 403
        response.put("error", e.getStatus().getReasonPhrase()); // 예: Forbidden
        response.put("message", e.getMessage());           // 🔴 우리가 적은 진짜 에러 메시지!

        return new ResponseEntity<>(response, e.getStatus());
    }
}