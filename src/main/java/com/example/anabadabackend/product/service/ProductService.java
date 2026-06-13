package com.example.anabadabackend.product.service;

import com.example.anabadabackend.auth.repository.UserRepository;
import com.example.anabadabackend.entity.User;
import com.example.anabadabackend.global.exception.EmailAuthException;
import com.example.anabadabackend.global.service.*;
import com.example.anabadabackend.product.dto.ProductCreateRequest;
import com.example.anabadabackend.product.dto.ProductResponse;
import com.example.anabadabackend.product.dto.ProductSearchCondition;
import com.example.anabadabackend.product.dto.ProductUpdateRequest;
import com.example.anabadabackend.product.entity.Product;
import com.example.anabadabackend.product.entity.ProductImage;
import com.example.anabadabackend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;
    private final RecentProductService recentProductService;


    @Transactional
    public ProductResponse create(Long userId, ProductCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EmailAuthException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        Product product = Product.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .price(request.getPrice())
                .hopeItem(request.getHopeItem())
                .tradeType(request.getTradeType())
                .category(request.getCategory())
                .user(user)
                .build();

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (String url : request.getImageUrls()) {
                ProductImage productImage = ProductImage.builder()
                        .imageUrl(url)
                        .build();
                product.addImage(productImage);
            }
        }

        productRepository.save(product);
        return new ProductResponse(product);
    }


    @Transactional
    public ProductResponse getProduct(Long userId, Long productId) {

        Product product = findProductById(productId);


        if (userId != null) {
            recentProductService.saveRecentProduct(userId, productId);
        }


        return new ProductResponse(product);
    }


    public List<ProductResponse> getRecentProducts(Long userId) {

        if (userId == null) {
            return List.of();
        }


        return recentProductService.getRecentProducts(userId);
    }


    public Page<ProductResponse> getProducts(ProductSearchCondition condition, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.search(
                condition.getCategory(),
                condition.getTradeType(),
                pageable
        ).map(ProductResponse::new);
    }


    @Transactional
    public ProductResponse update(Long userId, Long productId, ProductUpdateRequest request) {
        Product product = findProductById(productId);
        checkOwner(product, userId);

        List<String> oldImageUrls = product.getImages().stream()
                .map(ProductImage::getImageUrl)
                .toList();
        s3Uploader.deleteImages(oldImageUrls);
        product.clearImages();

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (String url : request.getImageUrls()) {
                ProductImage productImage = ProductImage.builder()
                        .imageUrl(url)
                        .build();
                product.addImage(productImage);
            }
        }

        product.update(
                request.getTitle(),
                request.getContent(),
                request.getPrice(),
                request.getHopeItem(),
                request.getTradeType(),
                request.getCategory(),
                request.getStatus()
        );

        return new ProductResponse(product);
    }


    @Transactional
    public void delete(Long userId, Long productId) {
        Product product = findProductById(productId);
        checkOwner(product, userId);

        List<String> imageUrls = product.getImages().stream()
                .map(ProductImage::getImageUrl)
                .toList();
        s3Uploader.deleteImages(imageUrls);

        productRepository.delete(product);
    }


    public Page<ProductResponse> getMyProducts(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByUserId(userId, pageable)
                .map(ProductResponse::new);
    }


    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EmailAuthException("게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }

    private void checkOwner(Product product, Long userId) {
        if (!product.getUser().getId().equals(userId)) {
            throw new EmailAuthException("본인의 게시글만 수정/삭제할 수 있습니다.", HttpStatus.FORBIDDEN);
        }
    }
}