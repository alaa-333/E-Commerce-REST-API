package com.ecommerce.api.dto.request.customer;

import com.ecommerce.api.entity.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class CreateCustomerRequest {

    @NotNull(message = "userId is required")
    @Positive(message = "userId must be a positive number")
    Long userId;

    @NotBlank(message = "phone is required")
    String phone;

    @NotNull(message = "address is required")
    Address address;
}
