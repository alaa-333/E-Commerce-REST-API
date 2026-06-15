package com.ecommerce.api.dto.request.cart;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCartItemRequest {
    @NotNull(message = "Quantity is required")
@Positive(message = "Quantity must be a positive number")
    private Integer quantity;
}
