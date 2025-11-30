package com.e_commerce.E_Commerce.REST.API.exception.orderItem;

import com.e_commerce.E_Commerce.REST.API.exception.BusinessException;
import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class OrderItemsEmptyException extends BusinessException {

    public OrderItemsEmptyException()
    {
        super(
                ErrorCode.ORDER_ITEMS_EMPTY,
                HttpStatus.BAD_REQUEST,
                ErrorCode.ORDER_ITEMS_EMPTY.getMessage()
        );
    }
}
