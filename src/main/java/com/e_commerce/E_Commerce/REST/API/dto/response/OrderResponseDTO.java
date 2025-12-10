package com.e_commerce.E_Commerce.REST.API.dto.response;

import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO
        (
                Long id,
                String orderNumber,
                LocalDateTime orderDate,
                BigDecimal totalAmount,
                String orderStatus,
                List<OrderItemResponseDTO> orderItems,
                PaymentResponseDTO payment,
                String formattedOrderDate,
                boolean isPaid

        ) {

    public OrderResponseDTO {
        // Validation
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException(ErrorCode.INVALID_PRICE);
        }

        // Default values
        if (orderItems == null) {
            orderItems = List.of();
        }

        // Derived data
        formattedOrderDate = orderDate != null ?
                orderDate.toString() : "N/A";
        isPaid = payment != null && "COMPLETED".equals(payment.paymentStatus());
    }

    // Business logic methods
    public boolean canBeCancelled() {
        return "PENDING".equals(orderStatus) || "CONFIRMED".equals(orderStatus);
    }

    public boolean isShippable() {
        return "PAID".equals(orderStatus) || "CONFIRMED".equals(orderStatus);
    }

    public BigDecimal calculateItemsTotal() {
        return orderItems.stream()
                .map(OrderItemResponseDTO::itemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
