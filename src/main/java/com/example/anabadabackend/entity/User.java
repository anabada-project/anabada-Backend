package com.example.anabadabackend.entity;

import com.example.anabadabackend.entity.enums.Gender;
import com.example.anabadabackend.entity.enums.Specialism;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 로그인 아이디
    @Column(nullable = false, unique = true, length = 30)
    private String userId;

    // 이메일
    @Column(nullable = false, unique = true, length = 30)
    private String email;

    // 이름
    @Column(nullable = false, length = 30)
    private String name;

    // 비밀번호
    @Column(nullable = false, length = 100)
    private String password;

    // 성별
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    // 전공
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Specialism specialism;

    // 기수
    @Column(nullable = false, length = 20)
    private String generation;

    @Builder
    public User(
            String userId,
            String email,
            String name,
            String password,
            Gender gender,
            Specialism specialism,
            String generation
    ) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.password = password;
        this.gender = gender;
        this.specialism = specialism;
        this.generation = generation;
    }
}