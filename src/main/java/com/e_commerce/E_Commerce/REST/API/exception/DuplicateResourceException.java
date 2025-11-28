package com.e_commerce.E_Commerce.REST.API.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends BusinessException{

    public DuplicateResourceException(ErrorCode errorCode, String resource , Object identifier)
    {
        super(errorCode, HttpStatus.CONFLICT, formatMessage(resource, identifier));
    }

    public DuplicateResourceException(String resource , Object identifier)
    {
        this(ErrorCode.RESOURCE_NOT_FOUND, resource, identifier);
    }



    // factory methods


    public static DuplicateResourceException forCustomer(String customerEmail)
    {
        return new DuplicateResourceException(
                ErrorCode.CUSTOMER_ALREADY_EXISTS,
                "customer",
                customerEmail
        );

    }

    public static DuplicateResourceException forProduct(String name)
    {
        return new DuplicateResourceException(
                ErrorCode.PRODUCT_ALREADY_EXISTS,
                "Product",
                name
        );
    }
}
