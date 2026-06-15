package com.ecommerce.api.repository;

import com.ecommerce.api.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("select c from Cart c join c.cartItems ci where ci.id = :itemId")
    Optional<Cart> findByCartItemId(@Param("itemId") Long itemId);
}
