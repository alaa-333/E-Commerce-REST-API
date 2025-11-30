package com.e_commerce.E_Commerce.REST.API.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Validation Errors (VAL-XXX)
    VALIDATION_FAILED("VAL-001", "Request validation failed", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT("VAL-002", "Invalid email format", HttpStatus.BAD_REQUEST),
    WEAK_PASSWORD("VAL-003", "Password does not meet security requirements", HttpStatus.BAD_REQUEST),
    REQUIRED_FIELD_MISSING("VAL-004", "Required field is missing", HttpStatus.BAD_REQUEST),
    INVALID_INPUT_FORMAT("VAL-005", "Invalid input format", HttpStatus.BAD_REQUEST),
    INVALID_DATE_FORMAT("VAL-006", "Invalid date format", HttpStatus.BAD_REQUEST),
    INVALID_NUMBER_FORMAT("VAL-007", "Invalid number format", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_NUMBER("VAL-010", "Invalid phone number format", HttpStatus.BAD_REQUEST),
    INVALID_URL("VAL-011", "Invalid URL format", HttpStatus.BAD_REQUEST),
    INVALID_POSTAL_CODE("VAL-012", "Invalid postal code format", HttpStatus.BAD_REQUEST),
    INVALID_COUNTRY_CODE("VAL-013", "Invalid country code", HttpStatus.BAD_REQUEST),
    INVALID_CURRENCY("VAL-014", "Invalid currency format", HttpStatus.BAD_REQUEST),
    INVALID_QUANTITY("VAL-015", "Invalid quantity value", HttpStatus.BAD_REQUEST),
    INVALID_PRICE_RANGE("VAL-016", "Invalid price range", HttpStatus.BAD_REQUEST),
    INVALID_SORT_PARAMETER("VAL-017", "Invalid sort parameter", HttpStatus.BAD_REQUEST),
    INVALID_FILTER_PARAMETER("VAL-018", "Invalid filter parameter", HttpStatus.BAD_REQUEST),
    INVALID_PAGINATION_PARAMETER("VAL-019", "Invalid pagination parameter", HttpStatus.BAD_REQUEST),
    DUPLICATE_ENTRY("VAL-020", "Duplicate entry found", HttpStatus.BAD_REQUEST),
    INVALID_ENUM_VALUE("VAL-021", "Invalid enum value", HttpStatus.BAD_REQUEST),
    INVALID_BOOLEAN_VALUE("VAL-022", "Invalid boolean value", HttpStatus.BAD_REQUEST),
    INVALID_PRICE("VAL-023", " Price is invalid", HttpStatus.BAD_REQUEST),


    // Authentication & Authorization Errors (AUTH-XXX)
    UNAUTHORIZED_ACCESS("AUTH-001", "Unauthorized access", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("AUTH-002", "Forbidden", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS("AUTH-003", "Invalid email or password", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("AUTH-004", "Authentication token expired", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("AUTH-005", "Invalid authentication token", HttpStatus.UNAUTHORIZED),
    ACCOUNT_LOCKED("AUTH-006", "Account is locked", HttpStatus.UNAUTHORIZED),

    // Business Logic Errors (BUS-XXX)
    UNREASONABLE_PRICE("BUS-001", "Price exceeds allowed maximum", HttpStatus.BAD_REQUEST),

    // Customer Logic Errors (CUS-XXX)
    CUSTOMER_ALREADY_EXISTS("CUS-001", "Customer already exists with this email", HttpStatus.CONFLICT),
    CUSTOMER_NOT_FOUND("CUS-002", "Customer not found", HttpStatus.NOT_FOUND),
    INVALID_CUSTOMER_DATA("CUS-003", "Customer data is invalid", HttpStatus.BAD_REQUEST),
    CUSTOMER_ADDRESS_NOT_FOUND("CUS-004", "Customer address not found", HttpStatus.NOT_FOUND),
    CUSTOMER_INACTIVE("CUS-5", "Customer account inactive", HttpStatus.FORBIDDEN),

    // Product Errors (PRO-XXX)
    PRODUCT_NOT_FOUND("PRO-001", "Product not found", HttpStatus.NOT_FOUND),
    INSUFFICIENT_PRODUCT_STOCK("PRO-002", "Insufficient product stock", HttpStatus.BAD_REQUEST),
    PRODUCT_DISABLED("PRO-003", "Product is disabled/unavailable", HttpStatus.BAD_REQUEST),
    PRODUCT_ALREADY_EXISTS("PRO-004", "A product with this name/code already exists", HttpStatus.CONFLICT),
    INVALID_PRODUCT_PRICE("PRO-005", "Product price is invalid", HttpStatus.BAD_REQUEST),
    PRODUCT_OUT_OF_STOCK("PRO-006", "Product is out of stock", HttpStatus.BAD_REQUEST),
    PRODUCT_QUANTITY_EXCEEDS_STOCK("PRO-7", "Stock quantity cannot exceed 100,000 units", HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_NAME("VAL-108", "Product name is invalid", HttpStatus.BAD_REQUEST),

    // Order Errors (ORD-XXX)
    ORDER_NOT_FOUND("ORD-001", "Order not found", HttpStatus.NOT_FOUND),
    ORDER_ALREADY_PROCESSED("ORD-002", "Order cannot be modified after processing", HttpStatus.CONFLICT),
    ORDER_PAYMENT_PENDING("ORD-003", "Order payment is still pending", HttpStatus.BAD_REQUEST),
    ORDER_ALREADY_CANCELLED("ORD-004", "Order has already been cancelled", HttpStatus.CONFLICT),
    ORDER_VALIDATION_FAILED("ORD-005", "Order validation failed", HttpStatus.BAD_REQUEST),
    ORDER_ITEMS_EMPTY("ORD-006", "Order must contain at least one item", HttpStatus.BAD_REQUEST),
    ORDER_TOTAL_INVALID("ORD-007", "Order total amount must be greater than zero", HttpStatus.BAD_REQUEST),

    // Payment Errors (PAY-XXX)
    PAYMENT_NOT_FOUND("PAY-001", "Payment not found", HttpStatus.NOT_FOUND),
    PAYMENT_FAILED("PAY-002", "Payment failed", HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_METHOD("PAY-003", "Invalid payment method", HttpStatus.BAD_REQUEST),
    PAYMENT_ALREADY_PROCESSED("PAY-004", "Payment already processed", HttpStatus.CONFLICT),
    PAYMENT_REFUND_FAILED("PAY-005", "Payment refund failed", HttpStatus.BAD_REQUEST),
    PAYMENT_AMOUNT_MISMATCH("BUS-005", "Payment amount does not match the expected value", HttpStatus.BAD_REQUEST),

    // Cart Errors (CRT-XXX)
    CART_NOT_FOUND("CRT-001", "Cart not found", HttpStatus.NOT_FOUND),
    CART_EMPTY("CRT-002", "Cart is empty", HttpStatus.BAD_REQUEST),
    CART_ITEM_NOT_FOUND("CRT-003", "Cart item not found", HttpStatus.NOT_FOUND),
    CART_ITEM_QUANTITY_EXCEEDS_STOCK("CRT-004", "Item quantity exceeds available stock", HttpStatus.BAD_REQUEST),

    // System & Infrastructure Errors (SYS-XXX)
    INTERNAL_SERVER_ERROR("SYS-001", "Unexpected server error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST("SYS-002", "Invalid request data", HttpStatus.BAD_REQUEST),
    DATABASE_ERROR("SYS-003", "Database error", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE("SYS-004", "Service temporarily unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    REQUEST_TIMEOUT("SYS-005", "Request timeout", HttpStatus.REQUEST_TIMEOUT),
    DATA_INTEGRITY_VIOLATION("SYS-006", "Data integrity violation", HttpStatus.CONFLICT),
    DATABASE_UNAVAILABLE("SYS-003", "Database connection failed", HttpStatus.SERVICE_UNAVAILABLE),
    EXTERNAL_SERVICE_ERROR("SYS-004", "External service unavailable", HttpStatus.SERVICE_UNAVAILABLE),

    // File & Resource Errors (RES-XXX)
    FILE_TOO_LARGE("RES-001", "File size exceeds maximum limit", HttpStatus.PAYLOAD_TOO_LARGE),
    INVALID_FILE_TYPE("RES-002", "File type not allowed", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("RES-003", "Requested resource not found", HttpStatus.NOT_FOUND),
    RESOURCE_ALREADY_EXISTS("RES-004", "resource already exists", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}