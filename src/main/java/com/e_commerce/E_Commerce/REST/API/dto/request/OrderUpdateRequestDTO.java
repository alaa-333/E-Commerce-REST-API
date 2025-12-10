package com.e_commerce.E_Commerce.REST.API.dto.request;

import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;
import com.e_commerce.E_Commerce.REST.API.model.enums.OrderStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class OrderUpdateRequestDTO {


    @Pattern(regexp = "PENDING|CONFIRMED|PAID|SHIPPED|CANCELLED",
            message = "Status must be one of: PENDING, CONFIRMED, PAID, SHIPPED, DELIVERED, CANCELLED")
    private String orderStatus;

    @AssertTrue(message = "Order status validation failed")
    private boolean isOrderStatusValid() {
        if (orderStatus == null || orderStatus.isBlank()) {
            throw new ValidationException(ErrorCode.REQUIRED_FIELD_MISSING);
        }

        try {
            OrderStatus.valueOf(orderStatus.toUpperCase());
            return true;
        } catch (ValidationException e) {
            throw new ValidationException(ErrorCode.INVALID_ENUM_VALUE);
        }
    }

    public void validateStatus() {
        if (!hasStatus()) {
            throw new ValidationException(ErrorCode.REQUIRED_FIELD_MISSING);
        }
        OrderStatus.valueOf(orderStatus.toUpperCase());
    }


    public boolean hasStatus() {
        return orderStatus != null && !orderStatus.isBlank();
    }
    public String getOrderStatus()
    {
        return orderStatus;
    }
}

