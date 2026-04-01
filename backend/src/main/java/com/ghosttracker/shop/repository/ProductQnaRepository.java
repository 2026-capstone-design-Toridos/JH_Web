package com.ghosttracker.shop.repository;

import com.ghosttracker.shop.entity.ProductQna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductQnaRepository extends JpaRepository<ProductQna, Long> {
    Page<ProductQna> findByProduct_IdOrderByCreatedAtDesc(Long productId, Pageable pageable);
    Page<ProductQna> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
