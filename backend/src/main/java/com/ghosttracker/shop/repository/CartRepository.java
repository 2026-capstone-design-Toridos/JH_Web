package com.ghosttracker.shop.repository;

import com.ghosttracker.shop.entity.Cart;
import com.ghosttracker.shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
    Optional<Cart> findByUser_Id(Long userId);
}
