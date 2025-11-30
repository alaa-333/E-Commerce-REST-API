package com.e_commerce.E_Commerce.REST.API.exception.product;

import com.e_commerce.E_Commerce.REST.API.exception.BusinessException;
import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class ProductOutOfStockException extends BusinessException {

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
