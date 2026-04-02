package com.ghosttracker.shop.repository;

import com.ghosttracker.shop.entity.Order;
import com.ghosttracker.shop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    Optional<Order> findByOrderNumber(String orderNumber);
    Optional<Order> findByOrderNumberAndUser(String orderNumber, User user);
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);
    long countByStatus(Order.OrderStatus status);
    Optional<Order> findByOrderNumberAndGuestEmail(String orderNumber, String guestEmail);
}
