package com.e_commerce.E_Commerce.REST.API.exception;

import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends BusinessException{

    public OrderNotFoundException(Object identifier)
    {
        super(ErrorCode.ORDER_NOT_FOUND, HttpStatus.NOT_FOUND, formatMessage("Order",identifier));
    }

    public static String formatMessage(String resource , Object identifier)
    {
        return "%s not found with identifier: %s".formatted(resource, identifier);
    }
}
