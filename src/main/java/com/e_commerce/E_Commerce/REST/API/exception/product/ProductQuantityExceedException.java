package com.e_commerce.E_Commerce.REST.API.exception.product;

import com.e_commerce.E_Commerce.REST.API.exception.BusinessException;
import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class ProductQuantityExceedException extends BusinessException {

    public ProductQuantityExceedException()
    {
        super(
                ErrorCode.PRODUCT_QUANTITY_EXCEEDS_STOCK,
                ErrorCode.PRODUCT_QUANTITY_EXCEEDS_STOCK.getHttpStatus(),
                ErrorCode.PRODUCT_QUANTITY_EXCEEDS_STOCK.getMessage()
        );
    }
}
