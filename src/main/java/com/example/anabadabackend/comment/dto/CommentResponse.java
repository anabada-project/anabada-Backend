package com.example.anabadabackend.comment.dto;

import com.example.anabadabackend.comment.entity.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse {

    @JsonProperty("comment_id")
    private final Long commentId;

    @JsonProperty("parent_comment_id")
    private final Long parentCommentId;

    @JsonProperty("comment_content")
    private final String commentContent;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    @JsonProperty("comment_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime commentTime;

    public CommentResponse(Comment comment) {
        this.commentId = comment.getId();
        this.parentCommentId = (comment.getParent() != null) ? comment.getParent().getId() : null;
        this.commentContent = comment.getCommentContent();
        this.createdAt = comment.getCreatedAt();
        this.commentTime = comment.getCreatedAt();
    }
}
