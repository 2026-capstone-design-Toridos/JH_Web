package com.ghosttracker.shop.controller;

import com.ghosttracker.shop.dto.order.CreateOrderRequest;
import com.ghosttracker.shop.dto.order.GuestOrderRequest;
import com.ghosttracker.shop.dto.order.OrderResponse;
import com.ghosttracker.shop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateOrderRequest req) {
        return ResponseEntity.ok(orderService.createOrder(userDetails.getUsername(), req));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<OrderResponse> orders = orderService.getMyOrders(userDetails.getUsername(), page, size);
        return ResponseEntity.ok(Map.of(
                "content", orders.getContent(),
                "totalPages", orders.getTotalPages(),
                "totalElements", orders.getTotalElements()
        ));
    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderDetail(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.getOrderDetail(userDetails.getUsername(), orderNumber));
    }

    // ── Guest order endpoints ──

    @PostMapping("/guest")
    public ResponseEntity<OrderResponse> createGuestOrder(
            @Valid @RequestBody GuestOrderRequest req) {
        return ResponseEntity.ok(orderService.createGuestOrder(req));
    }

    @GetMapping("/guest/{orderNumber}")
    public ResponseEntity<OrderResponse> getGuestOrderDetail(
            @PathVariable String orderNumber,
            @RequestParam String email) {
        return ResponseEntity.ok(orderService.getGuestOrderDetail(orderNumber, email));
    }

    @PostMapping("/{orderNumber}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.cancelOrder(userDetails.getUsername(), orderNumber));
    }
}
