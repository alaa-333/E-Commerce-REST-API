package com.ecommerce.api.repository;

import com.ecommerce.api.entity.Order;
import com.ecommerce.api.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByCustomer(Customer customer, Pageable pageable);

    org.springframework.data.domain.Page<Order> findByOrderStatus(com.ecommerce.api.entity.enums.OrderStatus status, Pageable pageable);

    long countByOrderDateBetween(LocalDateTime start, LocalDateTime end);
}


