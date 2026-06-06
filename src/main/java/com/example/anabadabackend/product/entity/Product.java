package com.example.anabadabackend.product.entity;

import com.example.anabadabackend.product.entity.enums.Category;
import com.example.anabadabackend.product.entity.enums.ProductStatus;
import com.example.anabadabackend.product.entity.enums.TradeType;
import jakarta.persistence.*;
import lombok.*;
import com.example.anabadabackend.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer price;

    @Column(length = 30)
    private String hopeItem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeType tradeType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;  // 기본값: 거래중

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // 이미지 추가 편의 메서드
    public void addImage(ProductImage image) {
        this.images.add(image);
        image.setProduct(this);
    }



    // 이미지 전체 삭제
    public void clearImages() {
        this.images.clear();
    }

    // 게시글 수정
    public void update(
            String title,
            String content,
            Integer price,
            String hopeItem,
            TradeType tradeType,
            Category category,
            ProductStatus status
    ) {
        this.title = title;
        this.content = content;
        this.price = price;
        this.hopeItem = hopeItem;
        this.tradeType = tradeType;
        this.category = category;
        this.status = status;
    }
}
