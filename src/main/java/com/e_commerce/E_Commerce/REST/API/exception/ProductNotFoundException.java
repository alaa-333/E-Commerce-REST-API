package com.e_commerce.E_Commerce.REST.API.exception;

import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends BusinessException{

    public ProductNotFoundException(Object identifier)
    {
        super(ErrorCode.PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND, formatMessage("Product",identifier));
    }

    public static String formatMessage(String resource , Object identifier)
    {
        return "%s not found with identifier: %s".formatted(resource, identifier);
    }
}
