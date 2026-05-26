package com.example.anabadabackend.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateRequest {

    @NotNull(message = "상품 ID는 필수입니다.")
    @JsonProperty("product_id")
    private Long productId;

    @NotBlank(message = "댓글 내용은 공백일 수 없습니다.")
    @JsonProperty("comment_content")
    private String commentContent;
}
