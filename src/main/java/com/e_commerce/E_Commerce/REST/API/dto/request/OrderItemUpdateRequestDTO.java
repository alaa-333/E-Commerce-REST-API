package com.e_commerce.E_Commerce.REST.API.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemUpdateRequestDTO {

    @NotNull(message = "Order quantity cannot be null")
    @Positive(message = "Order quantity must be positive")
    private Integer orderQuantity;
}
