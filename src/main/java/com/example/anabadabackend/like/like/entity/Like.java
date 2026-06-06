package com.example.anabadabackend.like.like.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "product_like",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    public Like(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }
}