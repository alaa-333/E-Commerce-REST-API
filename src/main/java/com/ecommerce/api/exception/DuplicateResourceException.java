package com.ecommerce.api.exception;


public class DuplicateResourceException extends EcommerceAppException{

    public DuplicateResourceException(ErrorCode errorCode, String detailedMessage) {
        super(errorCode, detailedMessage);
    }
}
