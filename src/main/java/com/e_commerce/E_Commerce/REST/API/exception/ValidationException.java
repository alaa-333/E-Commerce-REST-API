package com.e_commerce.E_Commerce.REST.API.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends BusinessException{

    public ValidationException(ErrorCode errorCode)
    {
        this(
                errorCode,
                errorCode.getMessage()
        );
    }

    public ValidationException(ErrorCode errorCode , String message)
    {
        super(
                errorCode,
                errorCode.getHttpStatus(),
                message
        );
    }


}
