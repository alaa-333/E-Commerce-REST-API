package com.e_commerce.E_Commerce.REST.API.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum ErrorCode {


    // Validation Errors (VAL-XXX)
    VALIDATION_FAILED("VAL-001", "Request validation failed"),
    INVALID_EMAIL_FORMAT("VAL-002", "Invalid email format"),
    WEAK_PASSWORD("VAL-003", "Password does not meet security requirements"),
    REQUIRED_FIELD_MISSING("VAL-004", "Required field is missing"),


    // Authentication & Authorization Errors (AUTH-XXX)
    UNAUTHORIZED_ACCESS("AUTH-001", "Unauthorized access"),
    ACCESS_DENIED("AUTH-002", "Forbidden"),
    INVALID_CREDENTIALS("AUTH-003", "Invalid email or password"),
    TOKEN_EXPIRED("AUTH-004", "Authentication token expired"),
    TOKEN_INVALID("AUTH-005", "Invalid authentication token"),
    ACCOUNT_LOCKED("AUTH-006", "Account is locked"),


    // Business Logic Errors (BUS-XXX)
    CUSTOMER_ALREADY_EXISTS("CUS-001", "Customer already exists with this email"),
    CUSTOMER_NOT_FOUND("CUS-002", "Customer not found"),
    INVALID_CUSTOMER_DATA("CUS-003", "Customer data is invalid"),
    CUSTOMER_ADDRESS_NOT_FOUND("CUS-004", "Customer address not found"),
    CUSTOMER_INACTIVE("CUS-5", "Customer account inactive"),



    PRODUCT_NOT_FOUND("PRO-001", "Product not found"),
    INSUFFICIENT_STOCK("PRO-002", "Insufficient product stock"),
    PRODUCT_DISABLED("PRO-003", "Product is disabled/unavailable"),
    PRODUCT_ALREADY_EXISTS("PRO-004", "A product with this name/code already exists"),
    INVALID_PRODUCT_PRICE("PRO-005", "Product price is invalid"),
    PRODUCT_OUT_OF_STOCK("BUS-006", "Product is out of stock"),


    ORDER_NOT_FOUND("ORD-001", "Order not found"),
    ORDER_ALREADY_PROCESSED("ORD-002", "Order cannot be modified after processing"),
    ORDER_PAYMENT_PENDING("ORD-003", "Order payment is still pending"),
    ORDER_ALREADY_CANCELLED("ORD-004", "Order has already been cancelled"),
    ORDER_VALIDATION_FAILED("ORD-005", "Order validation failed"),


    PAYMENT_NOT_FOUND("PAY-001", "Payment not found"),
    PAYMENT_FAILED("PAY-002", "Payment failed"),
    INVALID_PAYMENT_METHOD("PAY-003", "Invalid payment method"),
    PAYMENT_ALREADY_PROCESSED("PAY-004", "Payment already processed"),
    PAYMENT_REFUND_FAILED("PAY-005", "Payment refund failed"),
    PAYMENT_AMOUNT_MISMATCH("BUS-005", "Payment amount does not match the expected value"),


    CART_NOT_FOUND("CRT-001", "Cart not found"),
    CART_EMPTY("CRT-002", "Cart is empty"),
    CART_ITEM_NOT_FOUND("CRT-003", "Cart item not found"),
    CART_ITEM_QUANTITY_EXCEEDS_STOCK("CRT-004", "Item quantity exceeds available stock"),


    // System & Infrastructure Errors (SYS-XXX)
    INTERNAL_SERVER_ERROR("SYS-001", "Unexpected server error occurred"),
    BAD_REQUEST("SYS-002", "Invalid request data"),
    DATABASE_ERROR("SYS-003", "Database error"),
    SERVICE_UNAVAILABLE("SYS-004", "Service temporarily unavailable"),
    REQUEST_TIMEOUT("SYS-005", "Request timeout"),
    DATA_INTEGRITY_VIOLATION("SYS-006", "Data integrity violation"),
    DATABASE_UNAVAILABLE("SYS-003", "Database connection failed"),
    EXTERNAL_SERVICE_ERROR("SYS-004", "External service unavailable"),



    // File & Resource Errors (RES-XXX)
    FILE_TOO_LARGE("RES-001", "File size exceeds maximum limit"),
    INVALID_FILE_TYPE("RES-002", "File type not allowed"),
    RESOURCE_NOT_FOUND("RES-003", "Requested resource not found"),
    RESOURCE_ALREADY_EXISTS("RES-004", "resource already exists ");



    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
