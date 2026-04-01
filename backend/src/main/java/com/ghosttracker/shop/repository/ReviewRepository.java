package com.ghosttracker.shop.repository;

import com.ghosttracker.shop.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByProduct_IdOrderByCreatedAtDesc(Long productId, Pageable pageable);
    boolean existsByUser_IdAndOrderItem_Id(Long userId, Long orderItemId);
    List<Review> findByUser_Id(Long userId);
}
