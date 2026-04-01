package com.ghosttracker.shop.repository;

import com.ghosttracker.shop.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUser_IdOrderByCreatedAtDesc(Long userId);
    Optional<Wishlist> findByUser_IdAndProduct_Id(Long userId, Long productId);
    boolean existsByUser_IdAndProduct_Id(Long userId, Long productId);
    void deleteByUser_IdAndProduct_Id(Long userId, Long productId);

    @Query("SELECT w.product.id FROM Wishlist w WHERE w.user.id = :userId")
    List<Long> findProductIdsByUserId(@Param("userId") Long userId);
}
