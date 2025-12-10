package com.e_commerce.E_Commerce.REST.API.repository;

import com.e_commerce.E_Commerce.REST.API.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByProductId(Long productId);

    List<OrderItem> getItemsByOrderIdAndProductId(Long orderId, Long productId);
    Optional<OrderItem> findByOrderIdAndProductId(Long orderId, Long productId);
    Optional<OrderItem> findByIdAndOrderId(Long orderItemId, Long orderId);

    // to get orderItem with product details
    @Query("select oi from OrderItem oi join fetch oi.product where oi.id = :id")
    Optional<OrderItem> findByIdWithProduct(@Param("id") Long OrderItemId);


    // to get orderItem with Order details
    @Query("select oi from OrderItem oi join fetch oi.order where oi.id = :id")
    Optional<OrderItem> findByIdWithOrder(@Param("id") Long OrderItemId);


}
