package com.e_commerce.E_Commerce.REST.API.exception;

import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

public class PaymentAmountMismatchException extends BusinessException{

    public PaymentAmountMismatchException(BigDecimal expected, BigDecimal actual)
    {
        super(
                ErrorCode.PAYMENT_AMOUNT_MISMATCH,
                HttpStatus.BAD_REQUEST,
                formatMessge(expected,actual)

        );
    }

    private static String formatMessge(BigDecimal expected, BigDecimal actual)
    {
        return "Payment amount mismatch: expected %s, got %s".formatted(expected,actual);
    }
}
