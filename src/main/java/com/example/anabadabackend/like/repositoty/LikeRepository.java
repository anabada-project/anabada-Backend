package com.example.anabadabackend.like.repositoty;

import com.example.anabadabackend.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserIdAndProductId(Long userId, Long productId);

    List<Like> findByUserId(Long userId);
}