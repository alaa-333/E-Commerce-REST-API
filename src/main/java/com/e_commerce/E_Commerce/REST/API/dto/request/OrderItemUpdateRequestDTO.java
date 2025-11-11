package com.e_commerce.E_Commerce.REST.API.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemUpdateRequestDTO {

    @NotBlank(message = "order Quantity must be not blank")
    @Positive(message = "order Quantity must be positive value")
    private Integer orderQuantity;


}
