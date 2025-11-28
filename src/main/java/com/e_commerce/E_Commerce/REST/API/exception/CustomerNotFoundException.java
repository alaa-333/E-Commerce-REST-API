package com.e_commerce.E_Commerce.REST.API.exception;

import org.springframework.http.HttpStatus;


public class CustomerNotFoundException extends  BusinessException {


    public CustomerNotFoundException(Object identifier)
    {
        super(ErrorCode.CUSTOMER_NOT_FOUND, HttpStatus.NOT_FOUND, formatMessage("Customer", identifier));
    }

    public static String formatMessage(String resource , Object identifier)
    {
        return "%s not found with identifier: %s".formatted(resource, identifier);
    }

}


