package com.ghosttracker.shop.service;

import com.ghosttracker.shop.dto.product.ProductResponse;
import com.ghosttracker.shop.entity.*;
import com.ghosttracker.shop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<ProductResponse> getWishlist(String email) {
        User user = getUser(email);
        return wishlistRepository.findByUser_IdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(w -> ProductResponse.from(w.getProduct()))
                .toList();
    }

    public List<Long> getWishlistProductIds(String email) {
        User user = getUser(email);
        return wishlistRepository.findProductIdsByUserId(user.getId());
    }

    @Transactional
    public Map<String, Object> toggle(String email, Long productId) {
        User user = getUser(email);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        boolean exists = wishlistRepository.existsByUser_IdAndProduct_Id(user.getId(), productId);
        if (exists) {
            wishlistRepository.deleteByUser_IdAndProduct_Id(user.getId(), productId);
            return Map.of("liked", false, "message", "찜 목록에서 제거되었습니다.");
        } else {
            Wishlist w = Wishlist.builder().user(user).product(product).build();
            wishlistRepository.save(w);
            return Map.of("liked", true, "message", "찜 목록에 추가되었습니다.");
        }
    }

    public boolean isLiked(String email, Long productId) {
        User user = getUser(email);
        return wishlistRepository.existsByUser_IdAndProduct_Id(user.getId(), productId);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
