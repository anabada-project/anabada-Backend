package com.example.anabadabackend.auth.repository;

import com.example.anabadabackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 회원 조회
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(String userId);
    // 이메일 중복 검사
    boolean existsByEmail(String email);
}