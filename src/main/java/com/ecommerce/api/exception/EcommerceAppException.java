package com.ecommerce.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EcommerceAppException extends RuntimeException{

    private  ErrorCode errorCode;
    private  HttpStatus status;
    private  String detailedMessage;

    public EcommerceAppException(ErrorCode errorCode) {
        super(errorCode.getMessage() != null ? errorCode.getMessage():"unknown error");

    }

    public EcommerceAppException( ErrorCode errorCode, String detailedMessage) {
        super(detailedMessage != null ? detailedMessage:errorCode.getMessage());
        this.errorCode = errorCode;
        this.status = errorCode.getStatus();
        this.detailedMessage = detailedMessage;
    }

    public EcommerceAppException( ErrorCode errorCode,  HttpStatus status, String detailedMessage) {
        this(errorCode, detailedMessage);
        this.status = status;

    }



}
