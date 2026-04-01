package com.ghosttracker.shop.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrderRequest {
    @NotBlank
    private String receiverName;

    @NotBlank
    private String receiverPhone;

    @NotBlank
    private String shippingAddress;

    private String shippingAddressDetail;

    @NotBlank
    private String zipCode;

    private String deliveryRequest;

    private String paymentMethod;
}
