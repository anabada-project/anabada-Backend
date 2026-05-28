package com.example.anabadabackend.like.service;

import com.example.anabadabackend.like.dto.LikeResponse;
import com.example.anabadabackend.like.entity.Like;
import com.example.anabadabackend.like.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final LikeRepository likeRepository;

    public List<LikeResponse> getLikeListByUser(Long userId) {
        return likeRepository.findByUserIdOrderByFavoriteTimeDesc(userId).stream()
                .map(LikeResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<LikeResponse> addLike(Long userId, Long productId) {
        Optional<Like> alreadyLike = likeRepository.findByUserIdAndProductId(userId, productId);

        if (alreadyLike.isEmpty()) {
            likeRepository.save(new Like(userId, productId));
        }

        return getLikeListByUser(userId);
    }

    @Transactional
    public List<LikeResponse> deleteLike(Long userId, Long productId) {
        Optional<Like> alreadyLike = likeRepository.findByUserIdAndProductId(userId, productId);

        alreadyLike.ifPresent(likeRepository::delete);

        return getLikeListByUser(userId);
    }
}
