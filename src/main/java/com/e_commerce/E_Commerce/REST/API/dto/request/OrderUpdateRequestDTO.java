package com.e_commerce.E_Commerce.REST.API.dto.request;

import com.e_commerce.E_Commerce.REST.API.model.enums.OrderStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class OrderUpdateRequestDTO {


    @Pattern(regexp = "PENDING|CONFIRMED|PAID|SHIPPED|CANCELLED",
            message = "Status must be one of: PENDING, CONFIRMED, PAID, SHIPPED, DELIVERED, CANCELLED")
    private String orderStatus;

    @AssertTrue(message = "Order status is required when provided")
    private boolean isOrderStatusValidWhenPresent() {
        if (orderStatus != null && !orderStatus.isBlank()) {
            try {
                OrderStatus.valueOf(orderStatus.toUpperCase());
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return true;
    }


    public boolean hasStatus() {
        return orderStatus != null && !orderStatus.isBlank();
    }
    public String getOrderStatus()
    {
        return orderStatus;
    }
}

