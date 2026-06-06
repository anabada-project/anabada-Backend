package com.example.anabadabackend.product.dto;

import com.example.anabadabackend.product.entity.enums.Category;
import com.example.anabadabackend.product.entity.enums.ProductStatus;
import com.example.anabadabackend.product.entity.enums.TradeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;
@Getter
@NoArgsConstructor
public class ProductUpdateRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 20, message = "제목은 20자 이내로 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @NotNull(message = "가격을 입력해주세요.")
    private Integer price;

    @Size(max = 30, message = "희망물품은 30자 이내로 입력해주세요.")
    private String hopeItem;

    @NotNull(message = "거래방식을 선택해주세요.")
    private TradeType tradeType;

    @NotNull(message = "카테고리를 선택해주세요.")
    private Category category;

    @NotNull(message = "거래상태를 선택해주세요.")
    private ProductStatus status;   // ACTIVE / COMPLETED

    private List<String> imageUrls;  // S3 업로드 후 받은 URL 목록
}
