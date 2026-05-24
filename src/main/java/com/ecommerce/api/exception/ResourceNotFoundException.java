package com.ecommerce.api.exception;

public class ResourceNotFoundException extends EcommerceAppException{
    public ResourceNotFoundException(ErrorCode errorCode, String detailedMessage) {
        super(errorCode, detailedMessage);
    }
}
