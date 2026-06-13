package com.example.anabadabackend.product.service;

import com.example.anabadabackend.product.dto.ProductResponse;
import com.example.anabadabackend.product.entity.Product;
import com.example.anabadabackend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecentProductService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ProductRepository productRepository;

    private static final String KEY_PREFIX = "recent:product:";
    private static final int MAX_SIZE = 10;
    private static final Duration TTL = Duration.ofDays(3);


    public void saveRecentProduct(Long userId, Long productId) {
        String key = KEY_PREFIX + userId;
        double score = System.currentTimeMillis();

        ZSetOperations<String, String> zSet = redisTemplate.opsForZSet();


        zSet.add(key, String.valueOf(productId), score);


        Long size = zSet.zCard(key);
        if (size != null && size > MAX_SIZE) {
            zSet.removeRange(key, 0, size - MAX_SIZE - 1);
        }


        redisTemplate.expire(key, TTL);
    }


    public List<ProductResponse> getRecentProducts(Long userId) {
        String key = KEY_PREFIX + userId;

        Set<String> productIds = redisTemplate.opsForZSet()
                .reverseRange(key, 0, MAX_SIZE - 1);

        if (productIds == null || productIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> ids = productIds.stream()
                .map(Long::parseLong)
                .toList();

        List<ProductResponse> result = new ArrayList<>();

        productRepository.findAllById(ids)
                .forEach(product ->
                        result.add(new ProductResponse(product)));

        return result;
    }
}
