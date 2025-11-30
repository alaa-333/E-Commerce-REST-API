package com.e_commerce.E_Commerce.REST.API.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException{

    public ResourceNotFoundException(ErrorCode errorCode,HttpStatus status,String message)
    {
        super(
                errorCode,
                status,
                message
        );
    }
}
