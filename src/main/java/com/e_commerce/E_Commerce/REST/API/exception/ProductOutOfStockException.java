package com.e_commerce.E_Commerce.REST.API.exception;

import org.springframework.http.HttpStatus;

public class ProductOutOfStockException extends BusinessException{

    public ProductOutOfStockException(String productName)
    {
        super(
                ErrorCode.PRODUCT_OUT_OF_STOCK,
                HttpStatus.UNPROCESSABLE_ENTITY,
                formatMessage(productName)

        );
    }

    private static String formatMessage(String productName )
    {
        return "Product %s is out of stock".formatted(productName);
    }
}
