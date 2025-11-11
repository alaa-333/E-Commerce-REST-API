package com.e_commerce.E_Commerce.REST.API.dto.response;

import java.math.BigDecimal;

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
            throw new IllegalArgumentException("Product name cannot be blank");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
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
        if (stockQuantity < 10) {
            return "LOW_STOCK";
        }
        return "IN_STOCK";
    }

    // Business logic methods
    public boolean canBeOrdered() {
        return active && stockQuantity > 0;
    }

    public boolean isLowStock() {
        return active && stockQuantity > 0 && stockQuantity < 10;
    }

    public String getStockLevel() {
        if (!active) return "Not Available";
        if (stockQuantity == 0) return "Out of Stock";
        if (stockQuantity < 10) return "Low Stock (" + stockQuantity + " left)";
        return "In Stock (" + stockQuantity + " available)";
    }

    }
