package com.e_commerce.E_Commerce.REST.API.exception.order;

import com.e_commerce.E_Commerce.REST.API.exception.BusinessException;
import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;

public class OrderNotFoundException  extends ResourceNotFoundException {

    public OrderNotFoundException(Object identifier)
    {
        super(ErrorCode.ORDER_NOT_FOUND, HttpStatus.NOT_FOUND, formatMessage("Order",identifier));
    }

    public static String formatMessage(String resource , Object identifier)
    {
        return "%s not found with identifier: %s".formatted(resource, identifier);
    }
}
