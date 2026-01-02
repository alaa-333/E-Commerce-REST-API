package com.e_commerce.E_Commerce.REST.API.exception;

public class DuplicateResourceException extends BusinessException{

    public DuplicateResourceException(ErrorCode errorCode, String resource , Object identifier)
    {
        super(errorCode, errorCode.getHttpStatus(), formatMessage(resource, identifier));
    }

    public DuplicateResourceException(String resource , Object identifier)
    {
        this(ErrorCode.DUPLICATE_ENTRY, resource, identifier);
    }


    private static String formatMessage(String resource, Object identifier)
    {
        return "%s is already exist with identifier: %s ".formatted(resource,identifier);
    }


    // factory methods


    public static DuplicateResourceException forCustomer(String customerEmail)
    {
        return new DuplicateResourceException(
                ErrorCode.USER_ALREADY_EXISTS,
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
