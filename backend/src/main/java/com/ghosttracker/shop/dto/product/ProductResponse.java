package com.ghosttracker.shop.dto.product;

import com.ghosttracker.shop.entity.Product;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer stock;
    private String brand;
    private String mainImage;
    private String status;
    private Long categoryId;
    private String categoryName;
    private List<String> images;
    private double averageRating;
    private int reviewCount;
    private LocalDateTime createdAt;

    public static ProductResponse from(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .discountPrice(p.getDiscountPrice())
                .stock(p.getStock())
                .brand(p.getBrand())
                .mainImage(p.getMainImage())
                .status(p.getStatus().name())
                .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                .images(p.getImages().stream().map(img -> img.getImageUrl()).toList())
                .averageRating(p.getAverageRating())
                .reviewCount(p.getReviewCount())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
