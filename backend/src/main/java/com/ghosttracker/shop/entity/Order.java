package com.ghosttracker.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 0)
    private BigDecimal totalAmount;

    @Column(precision = 10, scale = 0)
    @Builder.Default
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    // Shipping info
    @Column(nullable = false, length = 100)
    private String receiverName;

    @Column(nullable = false, length = 20)
    private String receiverPhone;

    @Column(nullable = false, length = 255)
    private String shippingAddress;

    @Column(length = 100)
    private String shippingAddressDetail;

    @Column(length = 10)
    private String zipCode;

    @Column(length = 200)
    private String deliveryRequest;

    // Guest order info (null for member orders)
    @Column(length = 100)
    private String guestEmail;

    @Column(length = 50)
    private String guestName;

    @Column(length = 20)
    private String guestPhone;

    // Payment info
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.MOCK;

    @Column(length = 100)
    private String paymentKey;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum OrderStatus {
        PENDING, PAID, PREPARING, SHIPPING, DELIVERED, CANCELLED, REFUNDED
    }

    public enum PaymentMethod {
        MOCK, CARD, VIRTUAL_ACCOUNT, TRANSFER
    }
}
