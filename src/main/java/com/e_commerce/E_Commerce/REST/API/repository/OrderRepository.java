package com.e_commerce.E_Commerce.REST.API.repository;

import com.e_commerce.E_Commerce.REST.API.model.Order;
import com.e_commerce.E_Commerce.REST.API.model.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(Long customerId);

    Page<Order> findByOrderStatus(OrderStatus orderStatus, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN FETCH o.customer WHERE o.id = :id")
    Optional<Order> findByIdWithCustomer(@Param("id") Long id);

    @Query("SELECT o FROM Order o JOIN FETCH o.customer")
    Page<Order> findAllWithCustomer(Pageable pageable);

    @Query("SELECT o FROM Order o JOIN FETCH o.customer JOIN FETCH o.orderItems WHERE o.id = :id")
    Optional<Order> findByIdWithCustomerAndItems(@Param("id") Long id);

    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.customer.id = :customerId")
    Long countByCustomerId(@Param("customerId") Long customerId);
}