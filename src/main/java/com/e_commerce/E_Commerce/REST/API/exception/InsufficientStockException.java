package com.e_commerce.E_Commerce.REST.API.exception;

import org.springframework.http.HttpStatus;

public class InsufficientStockException extends BusinessException{

    public InsufficientStockException(String productName, int requested, int available)
    {
        super(ErrorCode.INSUFFICIENT_STOCK,
                HttpStatus.UNPROCESSABLE_ENTITY,
                formatMessage(productName,requested,available)
        );
    }

    private static String formatMessage(String productName, int requested, int available)
    {
        return "Insufficient stock for '%s': requested %d, available %d".formatted(productName,requested,available);
    }
}
