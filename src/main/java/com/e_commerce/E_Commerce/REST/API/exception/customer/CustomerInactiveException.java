package com.e_commerce.E_Commerce.REST.API.exception.customer;

import com.e_commerce.E_Commerce.REST.API.exception.BusinessException;
import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class CustomerInactiveException extends BusinessException {

    public CustomerInactiveException(Object email)
    {
        super(
                ErrorCode.CUSTOMER_INACTIVE,
                HttpStatus.BAD_REQUEST,
                formatMessage("Customer", email)
        );
    }

    public static String formatMessage(String resource, Object identifier)
    {
        return "%s account inactive with identifier: %s ".formatted(resource, identifier);
    }
}
