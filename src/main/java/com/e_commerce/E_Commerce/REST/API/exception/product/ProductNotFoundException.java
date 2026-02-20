package com.e_commerce.E_Commerce.REST.API.exception.product;

import com.e_commerce.E_Commerce.REST.API.exception.BusinessException;
import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;

public class ProductNotFoundException  extends ResourceNotFoundException {

    public ProductNotFoundException(Object identifier)
    {
        super(ErrorCode.PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND, formatMessage("Product",identifier));
    }

    public static String formatMessage(String resource, Object identifier) {
        return "%s not found with identifier: %s".formatted(resource, identifier);
    }
}
