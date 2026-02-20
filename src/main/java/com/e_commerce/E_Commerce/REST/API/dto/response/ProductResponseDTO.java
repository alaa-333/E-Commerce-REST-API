package com.e_commerce.E_Commerce.REST.API.dto.response;

import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductResponseDTO
        (
                Long id,
                String name,
                String description,
                BigDecimal price,
                String formattedPrice,
                Integer stockQuantity,
                String category,
                String imgUrl,
                Boolean active,
                Integer totalOrders,
                String availabilityStatus,
                boolean inStock

        ) {

    public ProductResponseDTO {
        // Validation
        if (name == null || name.isBlank()) {
            throw new ValidationException(ErrorCode.INVALID_PRODUCT_NAME);
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException(ErrorCode.INVALID_PRODUCT_PRICE);
        }

        // Default values
        if (stockQuantity == null) {
            stockQuantity = 0;
        }
        if (active == null) {
            active = true;
        }
        if (totalOrders == null) {
            totalOrders = 0;
        }

        // Derived data
        formattedPrice = String.format("$%.2f", price);
        availabilityStatus = calculateAvailabilityStatus(active, stockQuantity);
        inStock = active && stockQuantity > 0;
    }
    private static String calculateAvailabilityStatus(Boolean active, Integer stockQuantity) {
        if (active == null || !active) {
            return "UNAVAILABLE";
        }
        if (stockQuantity == null || stockQuantity == 0) {
            return "OUT_OF_STOCK";
        }
        if (stockQuantity < 10  && stockQuantity > 0) {
            return "LOW_STOCK (" + stockQuantity + " left)";
        }
        return "IN_STOCK";
    }

    // Business logic methods
    public boolean canBeOrdered() {
        return active && stockQuantity > 0;
    }


    }
