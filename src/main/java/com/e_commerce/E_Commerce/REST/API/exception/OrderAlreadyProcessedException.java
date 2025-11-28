package com.e_commerce.E_Commerce.REST.API.exception;

import org.springframework.http.HttpStatus;

public class OrderAlreadyProcessedException extends BusinessException{

    public OrderAlreadyProcessedException(String ordferNumber)
    {
        super(ErrorCode.ORDER_ALREADY_PROCESSED,
                HttpStatus.CONFLICT,
                formatMessage(ordferNumber)
        );
    }

    private static String formatMessage(String orderNumber)
    {
        return "Order %s cannot be modified after processing".formatted(orderNumber);
    }

}
