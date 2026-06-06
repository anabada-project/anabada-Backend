package com.example.anabadabackend.product.dto;

import com.example.anabadabackend.product.entity.Product;
import com.example.anabadabackend.product.entity.enums.Category;
import com.example.anabadabackend.product.entity.enums.ProductStatus;
import com.example.anabadabackend.product.entity.enums.TradeType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ProductResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final Integer price;
    private final String hopeItem;
    private final TradeType tradeType;
    private final Category category;
    private final ProductStatus status;
    private final LocalDateTime createdAt;
    private final Long userId;
    private final String userName;
    private final List<String> imageUrls;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.content = product.getContent();
        this.price = product.getPrice();
        this.hopeItem = product.getHopeItem();
        this.tradeType = product.getTradeType();
        this.category = product.getCategory();
        this.status = product.getStatus();
        this.createdAt = product.getCreatedAt();
        this.userId = product.getUser().getId();
        this.userName = product.getUser().getName();
        this.imageUrls = product.getImages().stream()
                .map(image -> image.getImageUrl())
                .toList();
    }
}
