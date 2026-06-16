package com.example.anabadabackend.comment.service;

import com.example.anabadabackend.auth.repository.UserRepository;
import com.example.anabadabackend.comment.dto.CommentCreateRequest;
import com.example.anabadabackend.comment.dto.CommentResponse;
import com.example.anabadabackend.comment.dto.ReplyCreateRequest;
import com.example.anabadabackend.comment.entity.Comment;
import com.example.anabadabackend.comment.repository.CommentRepository;
import com.example.anabadabackend.entity.User;
import com.example.anabadabackend.product.entity.Product;
import com.example.anabadabackend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;   // 추가
    private final UserRepository userRepository;         // 추가

    @Transactional
    public CommentResponse createComment(Long productId, CommentCreateRequest request, Long userId) {
        Comment comment = Comment.builder()
                .productId(productId)
                .userId(userId)
                .commentContent(request.getCommentContent())
                .parent(null)
                .build();

        return toResponse(commentRepository.save(comment), userId);
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, CommentCreateRequest request) {
        Comment comment = getComment(commentId);
        comment.updateContent(request.getCommentContent());
        return toResponse(comment, comment.getUserId());
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

        return toResponse(commentRepository.save(reply), userId);
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
    }


    private CommentResponse toResponse(Comment comment, Long currentUserId) {
        Product product = productRepository.findById(comment.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        User user = userRepository.findById(comment.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        return new CommentResponse(
                comment,
                user.getName(),
                product.getUser().getId(),
                currentUserId
        );
    }
}
