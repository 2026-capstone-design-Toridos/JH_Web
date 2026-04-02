package com.ghosttracker.shop.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GuestOrderRequest {

    @NotBlank
    private String guestEmail;

    @NotBlank
    private String guestName;

    @NotBlank
    private String guestPhone;

    @NotBlank
    private String receiverName;

    @NotBlank
    private String receiverPhone;

    @NotBlank
    private String zipCode;

    @NotBlank
    private String shippingAddress;

    private String shippingAddressDetail;

    private String deliveryRequest;

    private String paymentMethod;

    @NotEmpty
    private List<GuestOrderItem> items;

    @Getter
    @Setter
    public static class GuestOrderItem {
        private Long productId;
        private int quantity;
        private String selectedSize;
        private String selectedColor;
    }
}
