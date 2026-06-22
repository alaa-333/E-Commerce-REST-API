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
    CUSTOMER_NOT_FOUND("CUS-003", "Customer not found", HttpStatus.NOT_FOUND),

    // ── Category (CAT-XXX) ──
    CATEGORY_ALREADY_EXISTS("CAT-001", "Category with the same name already exists", HttpStatus.CONFLICT),
    CATEGORY_NOT_FOUND("CAT-002", "Category not found", HttpStatus.NOT_FOUND),
    CATEGORY_DELETE_FAILED("CAT-003", "Cannot delete category with associated products", HttpStatus.BAD_REQUEST),


    // ── product (PROD-XXX) ──

    PRODUCT_NOT_FOUND("PROD-001", "Product not found", HttpStatus.NOT_FOUND),
    PRODUCT_ALREADY_EXISTS("PROD-002", "Product with the same name already exists", HttpStatus.CONFLICT),
    PRODUCT_DELETE_FAILED("PROD-003", "Cannot delete product that is associated with an order", HttpStatus.BAD_REQUEST),
    PRODUCT_INSUFFICIENT_STOCK("PROD-004", "Insufficient stock available for the requested product", HttpStatus.BAD_REQUEST),
    PRODUCT_DISABLED("PROD-005", "The requested product is currently unavailable", HttpStatus.BAD_REQUEST),

     // --- cart (CARD-XXX) ---
     CART_NOT_FOUND("CARD-001", "Cart not found", HttpStatus.NOT_FOUND),
     CART_ITEM_NOT_FOUND("CARD-002", "Cart item not found", HttpStatus.NOT_FOUND),
     CART_ITEM_INSUFFICIENT_STOCK("CARD-003", "Insufficient stock available for the requested product in cart", HttpStatus.BAD_REQUEST),
     CART_ITEM_PRODUCT_NOT_FOUND("CARD-004", "Product not found for the cart item", HttpStatus.NOT_FOUND),
     CART_ITEM_PRODUCT_DISABLED("CARD-004", "The requested product in cart is currently unavailable", HttpStatus.BAD_REQUEST),

    // --- wishlist (WISH-XXX) ---
    WISHLIST_NOT_FOUND("WISH-001", "Wishlist not found", HttpStatus.NOT_FOUND),
    WISHLIST_ITEM_NOT_FOUND("WISH-002", "Wishlist item not found", HttpStatus.NOT_FOUND),
    WISHLIST_ITEM_ALREADY_EXISTS("WISH-003", "Product already exists in wishlist", HttpStatus.CONFLICT),
    WISHLIST_ITEM_PRODUCT_NOT_FOUND("WISH-004", "Product not found for the wishlist item", HttpStatus.NOT_FOUND),
    WISHLIST_ITEM_PRODUCT_DISABLED("WISH-005", "The requested product in wishlist is currently unavailable", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
