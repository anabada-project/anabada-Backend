package com.example.anabadabackend.product.repository;

import com.example.anabadabackend.product.entity.Product;
import com.example.anabadabackend.product.entity.enums.Category;
import com.example.anabadabackend.product.entity.enums.TradeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 카테고리 + 거래방식 검색 (최신순)
    @Query("SELECT p FROM Product p WHERE " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:tradeType IS NULL OR p.tradeType = :tradeType) " +
            "ORDER BY p.createdAt DESC")
    Page<Product> search(
            @Param("category") Category category,
            @Param("tradeType") TradeType tradeType,
            Pageable pageable
    );

    // 내 게시글 조회 (최신순)
    @Query("SELECT p FROM Product p WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
    Page<Product> findByUserId(@Param("userId") Long userId, Pageable pageable);


}
