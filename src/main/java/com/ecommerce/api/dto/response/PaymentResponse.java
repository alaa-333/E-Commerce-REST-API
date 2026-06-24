package com.ecommerce.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentResponse(
        Long id,
        Long orderId,
        BigDecimal amount,
        String paymentMethod,
        String paymentStatus,
        String clientSecret,
        String transactionId,
        LocalDateTime paymentDate
) {
}


