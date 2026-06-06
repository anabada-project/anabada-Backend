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

    @Transactional
    public List<LikeResponse> toggleLike(Long userId, Long productId) {
        Optional<Like> alreadyLike = likeRepository.findByUserIdAndProductId(userId, productId);

        if (alreadyLike.isPresent()) {
            likeRepository.delete(alreadyLike.get());
        } else {
            likeRepository.save(new Like(userId, productId));
        }

        return likeRepository.findByUserId(userId).stream()
                .map(LikeResponse::from)
                .collect(Collectors.toList());
    }
}