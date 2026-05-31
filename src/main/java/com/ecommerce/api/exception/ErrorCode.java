package com.ecommerce.api.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // ── System (SYS-XXX) ──
    VALIDATION_FAILED("VAL-001", "Request validation failed", HttpStatus.BAD_REQUEST),
    WEAK_PASSWORD("VAL-002", "password too weak", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("SYS-001", "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_PASSWORD("VAL-003", "Invalid password / does not match", HttpStatus.BAD_REQUEST),

    // ── Authentication (Auth-XXX) ──
    INVALID_EMAIL_OR_PASSWORD("AUTH-001", "Invalid email or password", HttpStatus.UNAUTHORIZED),
    ACCOUNT_LOCKED("AUTH-002", "Account is locked", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("AUTH-003", "Refresh token expired", HttpStatus.UNAUTHORIZED),
    INVALID_OR_MALFORMED_REFRESH_TOKEN("AUTH-004", "Invalid/malformed refresh token", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED("AUTH-5", "You are not authorize", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("AUTH-6", "Access denied", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS("AUTH-7", "Invalid credentials", HttpStatus.UNAUTHORIZED),



    // ── User (CUS-XXX) ──
    USER_ALREADY_EXIST("CUS-001", "Email already registered", HttpStatus.CONFLICT),
    USER_NOT_FOUND("CUS-002", "User not found", HttpStatus.NOT_FOUND),
    CUSTOMER_NOT_FOUND("CUS-003", "Customer not found", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
