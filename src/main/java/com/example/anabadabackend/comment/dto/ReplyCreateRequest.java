package com.example.anabadabackend.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReplyCreateRequest {

    @NotBlank(message = "대댓글 내용은 공백일 수 없습니다.")
    @JsonProperty("content")
    private String content;
}
