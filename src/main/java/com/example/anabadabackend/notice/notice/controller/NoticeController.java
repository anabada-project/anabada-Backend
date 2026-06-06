package com.example.anabadabackend.notice.notice.controller;

import com.example.anabadabackend.global.response.ApiResponse;
import com.example.anabadabackend.notice.dto.NoticeRequestDto;
import com.example.anabadabackend.notice.dto.NoticeResponseDto;
import com.example.anabadabackend.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 공지 작성 (POST /api/notice) - ADMIN
    @PostMapping
    public ResponseEntity<ApiResponse<NoticeResponseDto>> create(
            @RequestBody NoticeRequestDto request) {
        NoticeResponseDto response = noticeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("공지사항이 등록되었습니다.", response));
    }

    // 공지 수정 (PATCH /api/notice/{notice_id}) - ADMIN
    @PatchMapping("/{notice_id}")
    public ResponseEntity<ApiResponse<NoticeResponseDto>> update(
            @PathVariable Long notice_id,
            @RequestBody NoticeRequestDto request) {
        NoticeResponseDto response = noticeService.update(notice_id, request);
        return ResponseEntity.ok(ApiResponse.ok("공지사항이 수정되었습니다.", response));
    }

    // 공지 전체 조회 (GET /api/notice)
    @GetMapping
    public ResponseEntity<ApiResponse<List<NoticeResponseDto>>> getAll() {
        List<NoticeResponseDto> response = noticeService.getAll();
        return ResponseEntity.ok(ApiResponse.ok("공지사항 목록 조회 성공", response));
    }

    // 공지 바로가기 (GET /api/notice/{notice_id})
    @GetMapping("/{notice_id}")
    public ResponseEntity<ApiResponse<NoticeResponseDto>> getOne(
            @PathVariable Long notice_id) {
        NoticeResponseDto response = noticeService.getOne(notice_id);
        return ResponseEntity.ok(ApiResponse.ok("공지사항 단건 조회 성공", response));
    }

    // 공지 삭제 (DELETE /api/notice/{notice_id}) - ADMIN
    @DeleteMapping("/{notice_id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long notice_id) {
        noticeService.delete(notice_id);
        return ResponseEntity.ok(ApiResponse.ok("공지사항이 삭제되었습니다.", null));
    }
}