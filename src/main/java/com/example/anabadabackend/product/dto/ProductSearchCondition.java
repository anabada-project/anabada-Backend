package com.example.anabadabackend.product.dto;

import com.example.anabadabackend.product.entity.enums.Category;
import com.example.anabadabackend.product.entity.enums.TradeType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ProductSearchCondition {

    private Category category;      // 카테고리 필터
    private TradeType tradeType;    // 거래방식 필터


}
