package com.e_commerce.E_Commerce.REST.API.repository;

import com.e_commerce.E_Commerce.REST.API.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment> findByOrderId(Long orderId);
}
