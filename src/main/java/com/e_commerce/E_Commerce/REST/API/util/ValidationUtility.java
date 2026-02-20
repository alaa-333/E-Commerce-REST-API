package com.e_commerce.E_Commerce.REST.API.util;

import com.e_commerce.E_Commerce.REST.API.dto.request.OrderItemCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;
import com.e_commerce.E_Commerce.REST.API.exception.order.OrderTotalInvalidException;
import com.e_commerce.E_Commerce.REST.API.exception.orderItem.OrderItemsEmptyException;

import java.math.BigDecimal;
import java.util.List;

/**
 * Utility class for common validation logic across the application.
 * Centralizes validation patterns to eliminate duplication.
 */
public class ValidationUtility {

    private ValidationUtility() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    /**
     * Validates that a list of order items is not null or empty.
     * @param orderItems the list of order items to validate
     * @throws OrderItemsEmptyException if the list is null or empty
     */
    public static void validateOrderItemsNotEmpty(List<OrderItemCreateRequestDTO> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new OrderItemsEmptyException();
        }
    }

    /**
     * Validates that all order items have positive quantities.
     * @param orderItems the list of order items to validate
     * @throws ValidationException if any item has a non-positive quantity
     */
    public static void validateOrderItemsQuantity(List<OrderItemCreateRequestDTO> orderItems) {
        if (orderItems != null) {
            for (OrderItemCreateRequestDTO item : orderItems) {
                if (item.getQuantity() == null || item.getQuantity() <= 0) {
                    throw new ValidationException(ErrorCode.INVALID_QUANTITY);
                }
            }
        }
    }

    /**
     * Validates that the total order amount is greater than zero.
     * @param orderItems the list of order items to calculate total from
     * @throws OrderTotalInvalidException if the total is zero or negative
     */
    public static void validateOrderTotalGreaterThanZero(List<OrderItemCreateRequestDTO> orderItems) {
        BigDecimal total = calculateOrderTotal(orderItems);
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OrderTotalInvalidException();
        }
    }

    /**
     * Calculates the total amount for a list of order items.
     * @param orderItems the list of order items
     * @return the total amount as BigDecimal
     */
    public static BigDecimal calculateOrderTotal(List<OrderItemCreateRequestDTO> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return orderItems.stream()
                .map(OrderItemCreateRequestDTO::getItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Validates that a quantity is positive.
     * @param quantity the quantity to validate
     * @throws ValidationException if quantity is null or non-positive
     */
    public static void validateQuantityPositive(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new ValidationException(ErrorCode.INVALID_QUANTITY);
        }
    }

    /**
     * Validates that a price is positive and reasonable.
     * @param price the price to validate
     * @throws ValidationException if price is null, non-positive, or exceeds maximum
     */
    public static void validatePricePositive(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(ErrorCode.INVALID_PRICE);
        }
    }

    /**
     * Validates that a string is not null or blank.
     * @param value the string to validate
     * @param fieldName the field name for error message
     * @throws ValidationException if value is null or blank
     */
    public static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(ErrorCode.REQUIRED_FIELD_MISSING);
        }
    }

    /**
     * Validates that a value is not null.
     * @param value the value to validate
     * @param fieldName the field name for error message
     * @throws ValidationException if value is null
     */
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(ErrorCode.REQUIRED_FIELD_MISSING);
        }
    }
}
