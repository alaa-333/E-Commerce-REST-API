package com.e_commerce.E_Commerce.REST.API.dto.request;

import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {

    @NotBlank(message = "Payment method is required")
    @Pattern(regexp = "^(CREDIT_CARD|CASH_WALLET|PAYPAL|STRIPE|)$",
            message = "Payment method must be one of: CREDIT_CARD, DEBIT_CARD, PAYPAL, STRIPE, BANK_TRANSFER")
    private String paymentMethod;


    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount must have up to 10 integer digits and 2 fraction digits")
    private BigDecimal amount;


    @NotNull(message = "Order ID is required")
    @Positive(message = "Order ID must be positive")
    private Long orderId;

    public void validatePaymentAmount()
    {

        if (amount == null)
        {
            throw new ValidationException(ErrorCode.REQUIRED_FIELD_MISSING, "Amount cannot be null");
        }


        if (amount.compareTo(BigDecimal.valueOf(50000)) > 0)
        {
            throw new ValidationException(ErrorCode.UNREASONABLE_PRICE);
        }


    }

    public void handlePaymentMethod()
    {
        if (!paymentMethod.isBlank())
        {
            this.paymentMethod = this.paymentMethod.trim().toUpperCase();
        }
    }

}

