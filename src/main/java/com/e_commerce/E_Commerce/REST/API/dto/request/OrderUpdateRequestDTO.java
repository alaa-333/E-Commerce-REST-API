package com.e_commerce.E_Commerce.REST.API.dto.request;

import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;
import com.e_commerce.E_Commerce.REST.API.model.enums.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateRequestDTO {

    @NotBlank(message = "Order status cannot be blank")
    @Pattern(regexp = "PENDING|CONFIRMED|PAID|SHIPPED|DELIVERED|CANCELLED",
            message = "Status must be one of: PENDING, CONFIRMED, PAID, SHIPPED, DELIVERED, CANCELLED")
    private String orderStatus;

    /**
     * Validates the order status against the OrderStatus enum
     * @throws ValidationException if status is invalid
     */
    public void validateStatus() {
        if (orderStatus == null || orderStatus.isBlank()) {
            throw new ValidationException(ErrorCode.REQUIRED_FIELD_MISSING);
        }
        
        try {
            OrderStatus.valueOf(orderStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(ErrorCode.INVALID_ENUM_VALUE);
        }
    }

    public boolean hasStatus() {
        return orderStatus != null && !orderStatus.isBlank();
    }
}

