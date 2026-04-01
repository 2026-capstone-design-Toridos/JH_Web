package com.ghosttracker.shop.service;

import com.ghosttracker.shop.dto.order.CreateOrderRequest;
import com.ghosttracker.shop.dto.order.OrderResponse;
import com.ghosttracker.shop.entity.*;
import com.ghosttracker.shop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    private static final BigDecimal FREE_SHIPPING_THRESHOLD = BigDecimal.valueOf(50000);
    private static final BigDecimal SHIPPING_FEE = BigDecimal.valueOf(3000);

    @Transactional
    public OrderResponse createOrder(String email, CreateOrderRequest req) {
        User user = getUser(email);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("장바구니가 비어 있습니다."));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("장바구니에 상품이 없습니다.");
        }

        // Calculate total
        BigDecimal total = cart.getItems().stream()
                .map(item -> {
                    BigDecimal price = item.getProduct().getDiscountPrice() != null
                            ? item.getProduct().getDiscountPrice()
                            : item.getProduct().getPrice();
                    return price.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shippingFee = total.compareTo(FREE_SHIPPING_THRESHOLD) >= 0
                ? BigDecimal.ZERO : SHIPPING_FEE;

        Order.PaymentMethod paymentMethod = Order.PaymentMethod.MOCK;
        if (req.getPaymentMethod() != null) {
            try { paymentMethod = Order.PaymentMethod.valueOf(req.getPaymentMethod()); }
            catch (Exception ignored) {}
        }

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .user(user)
                .totalAmount(total.add(shippingFee))
                .shippingFee(shippingFee)
                .status(Order.OrderStatus.PAID)
                .receiverName(req.getReceiverName())
                .receiverPhone(req.getReceiverPhone())
                .shippingAddress(req.getShippingAddress())
                .shippingAddressDetail(req.getShippingAddressDetail())
                .zipCode(req.getZipCode())
                .deliveryRequest(req.getDeliveryRequest())
                .paymentMethod(paymentMethod)
                .build();

        // Add items to order and reduce stock
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new IllegalStateException(product.getName() + " 상품의 재고가 부족합니다.");
            }
            product.setStock(product.getStock() - cartItem.getQuantity());

            BigDecimal unitPrice = product.getDiscountPrice() != null
                    ? product.getDiscountPrice() : product.getPrice();

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .price(unitPrice)
                    .quantity(cartItem.getQuantity())
                    .selectedSize(cartItem.getSelectedSize())
                    .selectedColor(cartItem.getSelectedColor())
                    .productImage(product.getMainImage())
                    .build();
            order.getItems().add(orderItem);
        }

        orderRepository.save(order);

        // Clear cart
        cart.getItems().clear();
        cartItemRepository.deleteByCart(cart);
        cartRepository.save(cart);

        return OrderResponse.from(order);
    }

    public Page<OrderResponse> getMyOrders(String email, int page, int size) {
        User user = getUser(email);
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findByUserOrderByCreatedAtDesc(user, pageable)
                .map(OrderResponse::from);
    }

    public OrderResponse getOrderDetail(String email, String orderNumber) {
        User user = getUser(email);
        Order order = orderRepository.findByOrderNumberAndUser(orderNumber, user)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse cancelOrder(String email, String orderNumber) {
        User user = getUser(email);
        Order order = orderRepository.findByOrderNumberAndUser(orderNumber, user)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (order.getStatus() != Order.OrderStatus.PAID) {
            throw new IllegalStateException("취소할 수 없는 주문 상태입니다.");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.getItems().forEach(item -> {
            if (item.getProduct() != null) {
                item.getProduct().setStock(item.getProduct().getStock() + item.getQuantity());
            }
        });

        return OrderResponse.from(orderRepository.save(order));
    }

    private String generateOrderNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", new Random().nextInt(9999));
        return "ORD" + date + random;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
