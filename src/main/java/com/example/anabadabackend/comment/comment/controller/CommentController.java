package com.example.anabadabackend.comment.comment.controller;

import com.example.anabadabackend.comment.dto.CommentCreateRequest;
import com.example.anabadabackend.comment.dto.CommentResponse;
import com.example.anabadabackend.comment.dto.ReplyCreateRequest;
import com.example.anabadabackend.comment.service.CommentService;
import com.example.anabadabackend.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @Valid @RequestBody CommentCreateRequest request
    ) {
        Long mockUserId = 1L;
        CommentResponse response = commentService.createComment(request.getProductId(), request, mockUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("댓글이 등록되었습니다.", response));
    }

    @PatchMapping("/{comment_id}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable(name = "comment_id") Long commentId,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        CommentResponse response = commentService.updateComment(commentId, request);
        return ResponseEntity.ok(ApiResponse.ok("댓글이 수정되었습니다.", response));
    }

    @DeleteMapping("/{comment_id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable(name = "comment_id") Long commentId
    ) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(ApiResponse.ok("댓글이 삭제되었습니다."));
    }

    @PostMapping("/{comment_id}")
    public ResponseEntity<ApiResponse<CommentResponse>> createReply(
            @PathVariable(name = "comment_id") Long commentId,
            @Valid @RequestBody ReplyCreateRequest request
    ) {
        Long mockUserId = 1L;
        CommentResponse response = commentService.createReply(commentId, request, mockUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("대댓글이 등록되었습니다.", response));
    }
}
