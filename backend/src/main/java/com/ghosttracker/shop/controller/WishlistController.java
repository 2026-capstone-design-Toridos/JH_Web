package com.ghosttracker.shop.controller;

import com.ghosttracker.shop.dto.product.ProductResponse;
import com.ghosttracker.shop.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getWishlist(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(wishlistService.getWishlist(userDetails.getUsername()));
    }

    @GetMapping("/ids")
    public ResponseEntity<List<Long>> getWishlistIds(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(wishlistService.getWishlistProductIds(userDetails.getUsername()));
    }

    @PostMapping("/{productId}/toggle")
    public ResponseEntity<Map<String, Object>> toggle(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {
        return ResponseEntity.ok(wishlistService.toggle(userDetails.getUsername(), productId));
    }

    @GetMapping("/{productId}/status")
    public ResponseEntity<Map<String, Object>> isLiked(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {
        boolean liked = wishlistService.isLiked(userDetails.getUsername(), productId);
        return ResponseEntity.ok(Map.of("liked", liked));
    }
}
