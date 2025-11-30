package com.e_commerce.E_Commerce.REST.API.exception.customer;

import com.e_commerce.E_Commerce.REST.API.exception.BusinessException;
import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;


public class CustomerNotFoundException extends ResourceNotFoundException {


    public CustomerNotFoundException(Object identifier)
    {
        super(ErrorCode.CUSTOMER_NOT_FOUND, HttpStatus.NOT_FOUND, formatMessage("Customer", identifier));
    }

    public static String formatMessage(String resource , Object identifier)
    {
        return "%s not found with identifier: %s".formatted(resource, identifier);
    }

}


