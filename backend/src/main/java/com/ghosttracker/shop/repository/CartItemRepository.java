package com.ghosttracker.shop.repository;

import com.ghosttracker.shop.entity.Cart;
import com.ghosttracker.shop.entity.CartItem;
import com.ghosttracker.shop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProductAndSelectedSizeAndSelectedColor(
            Cart cart, Product product, String selectedSize, String selectedColor);
    void deleteByCart(Cart cart);
}
