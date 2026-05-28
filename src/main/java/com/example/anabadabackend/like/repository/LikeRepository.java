package com.example.anabadabackend.like.repository;

import com.example.anabadabackend.like.entity.Like;
import com.example.anabadabackend.like.entity.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, LikeId> {

    Optional<Like> findByUserIdAndProductId(Long userId, Long productId);

    List<Like> findByUserIdOrderByFavoriteTimeDesc(Long userId);
}
