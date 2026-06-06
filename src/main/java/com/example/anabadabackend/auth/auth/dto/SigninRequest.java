package com.example.anabadabackend.auth.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SigninRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String id;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    // 테스트 코드 및 내부 사용용 생성자
    public SigninRequest(String id, String password) {
        this.id = id;
        this.password = password;
    }
}