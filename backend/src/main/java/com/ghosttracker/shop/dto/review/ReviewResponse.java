package com.ghosttracker.shop.dto.review;

import com.ghosttracker.shop.entity.Review;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {
    private Long id;
    private Long productId;
    private Long userId;
    private String userName;
    private Integer rating;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;

    public static ReviewResponse from(Review r) {
        return ReviewResponse.builder()
                .id(r.getId())
                .productId(r.getProduct().getId())
                .userId(r.getUser().getId())
                .userName(r.getUser().getName())
                .rating(r.getRating())
                .content(r.getContent())
                .imageUrl(r.getImageUrl())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
