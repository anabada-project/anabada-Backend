package com.example.anabadabackend.entity.entity;

import com.example.anabadabackend.entity.enums.Gender;
import com.example.anabadabackend.entity.enums.Specialism;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String userId;

    @Column(nullable = false, unique = true, length = 30)
    private String email;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Specialism specialism;

    @Column(nullable = false, length = 20)
    private String generation;

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

    /**
     * 비밀번호 변경
     */
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
