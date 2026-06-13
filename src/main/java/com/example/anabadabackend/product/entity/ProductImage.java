package com.example.anabadabackend.product.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "product_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;  // 이미지 경로 (예: /uploads/uuid.jpg)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Product.addImage() 편의 메서드에서 사용
    protected void setProduct(Product product) {
        this.product = product;
    }
}

