package com.ghosttracker.shop.service;

import com.ghosttracker.shop.dto.cart.AddCartItemRequest;
import com.ghosttracker.shop.dto.cart.CartResponse;
import com.ghosttracker.shop.entity.*;
import com.ghosttracker.shop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartResponse getCart(String email) {
        User user = getUser(email);
        Cart cart = getOrCreateCart(user);
        return CartResponse.from(cart);
    }

    @Transactional
    public CartResponse addItem(String email, AddCartItemRequest req) {
        User user = getUser(email);
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        if (product.getStock() < req.getQuantity()) {
            throw new IllegalStateException("재고가 부족합니다.");
        }

        var existingItem = cartItemRepository.findByCartAndProductAndSelectedSizeAndSelectedColor(
                cart, product, req.getSelectedSize(), req.getSelectedColor());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + req.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(req.getQuantity())
                    .selectedSize(req.getSelectedSize())
                    .selectedColor(req.getSelectedColor())
                    .build();
            cart.getItems().add(item);
        }

        cartRepository.save(cart);
        return CartResponse.from(cartRepository.findById(cart.getId()).orElse(cart));
    }

    @Transactional
    public CartResponse updateItemQuantity(String email, Long itemId, Integer quantity) {
        User user = getUser(email);
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 항목을 찾을 수 없습니다."));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }

        if (quantity <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        cartRepository.save(cart);
        return CartResponse.from(cartRepository.findById(cart.getId()).orElse(cart));
    }

    @Transactional
    public CartResponse removeItem(String email, Long itemId) {
        User user = getUser(email);
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 항목을 찾을 수 없습니다."));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }

        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        cartRepository.save(cart);

        return CartResponse.from(cartRepository.findById(cart.getId()).orElse(cart));
    }

    @Transactional
    public void clearCart(String email) {
        User user = getUser(email);
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        cartItemRepository.deleteByCart(cart);
        cartRepository.save(cart);
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart cart = Cart.builder().user(user).build();
            return cartRepository.save(cart);
        });
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
