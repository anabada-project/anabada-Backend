package com.example.anabadabackend.auth.auth.dto;

import com.example.anabadabackend.entity.enums.Gender;
import com.example.anabadabackend.entity.enums.Specialism;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {

    // 회원 이름
    private String name;

    private String id;
    // 회원 이메일
    private String email;

    // 회원 비밀번호
    private String password;

    // 회원 성별
    private Gender gender;

    // 회원 전공
    private Specialism specialism;

    // 회원 기수
    private String generation;
}