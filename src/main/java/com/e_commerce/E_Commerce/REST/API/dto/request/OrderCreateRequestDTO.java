package com.e_commerce.E_Commerce.REST.API.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public class OrderCreateRequestDTO {

    @NotNull(message = "Customer ID is required")
    @Positive(message = "Customer ID must be positive")
    private Long customerId;

    @NotNull(message = "Order items are required")
    private List<OrderItemCreateRequestDTO> orderItems;

    // Custom validation methods
    public boolean isValidOrder() {
        return orderItems != null && !orderItems.isEmpty() && calculateTotalAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal calculateTotalAmount() {
        if (orderItems == null || orderItems.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return orderItems.stream()
                .map(OrderItemCreateRequestDTO::getItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void validateStock() {
        if (orderItems != null) {
            for (OrderItemCreateRequestDTO item : orderItems) {
                if (item.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Quantity must be positive for product: " + item.getProductId());
                }
            }
        }
    }
}
