package com.ecommerce.api.dto.request.payment;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentRequest {

    @NotNull(message = "orderId is required")
    private Long orderId;

    @NotNull(message = "paymentMethod is required")
    private String paymentMethod;
}

