package com.e_commerce.E_Commerce.REST.API.exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Objects;

@Getter

public abstract class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final HttpStatus httpStatus;
    private final String detailsMessage;

    // base constructor

    protected BusinessException(ErrorCode errorCode, HttpStatus httpStatus)
    {
        this(errorCode, httpStatus , errorCode.getMessage());
    }

    // constructor with details msg
    protected BusinessException(ErrorCode errorCode, HttpStatus httpStatus , String detailsMessage)
    {
        // to ensure msg not include null
        super(detailsMessage != null ?  detailsMessage : (errorCode != null ?  errorCode.getMessage() : null ));
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;

        this.detailsMessage = detailsMessage != null ? detailsMessage : Objects.requireNonNull(errorCode).getMessage();
    }




}
