package com.example.anabadabackend.comment.service;

import com.example.anabadabackend.comment.dto.CommentCreateRequest;
import com.example.anabadabackend.comment.dto.CommentResponse;
import com.example.anabadabackend.comment.dto.ReplyCreateRequest;
import com.example.anabadabackend.comment.entity.Comment;
import com.example.anabadabackend.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponse createComment(Long productId, CommentCreateRequest request, Long userId) {
        Comment comment = Comment.builder()
                .productId(productId)
                .userId(userId)
                .commentContent(request.getCommentContent())
                .parent(null)
                .build();

        return new CommentResponse(commentRepository.save(comment));
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, CommentCreateRequest request) {
        Comment comment = getComment(commentId);
        comment.updateContent(request.getCommentContent());
        return new CommentResponse(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = getComment(commentId);
        commentRepository.delete(comment);
    }

    @Transactional
    public CommentResponse createReply(Long commentId, ReplyCreateRequest request, Long userId) {
        Comment parentComment = getComment(commentId);

        Comment reply = Comment.builder()
                .productId(parentComment.getProductId())
                .userId(userId)
                .commentContent(request.getContent())
                .parent(parentComment)
                .build();

        return new CommentResponse(commentRepository.save(reply));
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
    }
}
