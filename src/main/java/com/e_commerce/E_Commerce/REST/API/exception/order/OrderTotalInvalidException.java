package com.e_commerce.E_Commerce.REST.API.exception.order;

import com.e_commerce.E_Commerce.REST.API.exception.BusinessException;
import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class OrderTotalInvalidException extends BusinessException {

    public OrderTotalInvalidException()
    {
        super(
                ErrorCode.ORDER_TOTAL_INVALID,
                HttpStatus.BAD_REQUEST,
                ErrorCode.ORDER_TOTAL_INVALID.getMessage()
                );
    }
}
