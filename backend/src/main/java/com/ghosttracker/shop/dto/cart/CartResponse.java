package com.ghosttracker.shop.dto.cart;

import com.ghosttracker.shop.entity.Cart;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartResponse {
    private Long id;
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;
    private int totalCount;

    @Data
    @Builder
    public static class CartItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String productImage;
        private BigDecimal price;
        private BigDecimal discountPrice;
        private Integer quantity;
        private String selectedSize;
        private String selectedColor;
        private Integer stock;
    }

    public static CartResponse from(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream().map(item -> {
            var product = item.getProduct();
            return CartItemResponse.builder()
                    .id(item.getId())
                    .productId(product.getId())
                    .productName(product.getName())
                    .productImage(product.getMainImage())
                    .price(product.getPrice())
                    .discountPrice(product.getDiscountPrice())
                    .quantity(item.getQuantity())
                    .selectedSize(item.getSelectedSize())
                    .selectedColor(item.getSelectedColor())
                    .stock(product.getStock())
                    .build();
        }).toList();

        BigDecimal total = items.stream()
                .map(i -> {
                    BigDecimal unitPrice = i.getDiscountPrice() != null ? i.getDiscountPrice() : i.getPrice();
                    return unitPrice.multiply(BigDecimal.valueOf(i.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int count = items.stream().mapToInt(CartItemResponse::getQuantity).sum();

        return CartResponse.builder()
                .id(cart.getId())
                .items(items)
                .totalAmount(total)
                .totalCount(count)
                .build();
    }
}
