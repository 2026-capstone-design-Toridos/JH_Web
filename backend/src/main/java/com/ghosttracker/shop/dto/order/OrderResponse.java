package com.ghosttracker.shop.dto.order;

import com.ghosttracker.shop.entity.Order;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;
    private String shippingAddressDetail;
    private String zipCode;
    private String deliveryRequest;
    private String paymentMethod;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;

    @Data
    @Builder
    public static class OrderItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String productImage;
        private BigDecimal price;
        private Integer quantity;
        private String selectedSize;
        private String selectedColor;
    }

    public static OrderResponse from(Order order) {
        List<OrderItemResponse> items = order.getItems().stream().map(item ->
            OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .productName(item.getProductName())
                .productImage(item.getProductImage())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .selectedSize(item.getSelectedSize())
                .selectedColor(item.getSelectedColor())
                .build()
        ).toList();

        return OrderResponse.builder()
            .id(order.getId())
            .orderNumber(order.getOrderNumber())
            .status(order.getStatus().name())
            .totalAmount(order.getTotalAmount())
            .shippingFee(order.getShippingFee())
            .receiverName(order.getReceiverName())
            .receiverPhone(order.getReceiverPhone())
            .shippingAddress(order.getShippingAddress())
            .shippingAddressDetail(order.getShippingAddressDetail())
            .zipCode(order.getZipCode())
            .deliveryRequest(order.getDeliveryRequest())
            .paymentMethod(order.getPaymentMethod().name())
            .items(items)
            .createdAt(order.getCreatedAt())
            .build();
    }
}
