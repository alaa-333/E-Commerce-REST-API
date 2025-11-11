package com.e_commerce.E_Commerce.REST.API.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequestDTO {

    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have up to 10 integer digits and 2 fraction digits")
    private BigDecimal price;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @Size(min = 2, max = 50, message = "Category must be between 2 and 50 characters")
    private String category;

    @Pattern(regexp = "^(https?://.*\\.(?:png|jpg|jpeg|gif|webp))?$",
            message = "Image URL must be a valid URL ending with .png, .jpg, .jpeg, .gif, or .webp")
    private String imgUrl;

    private Boolean active;

    // Helper methods for partial updates
    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }

    public boolean hasPrice() {
        return price != null;
    }

    public boolean hasStockQuantity() {
        return stockQuantity != null;
    }

    public boolean hasCategory() {
        return category != null && !category.isBlank();
    }

    public boolean hasImgUrl() {
        return imgUrl != null && !imgUrl.isBlank();
    }

    public boolean hasActive() {
        return active != null;
    }

    // Custom validation for conditional fields
    @AssertTrue(message = "Price must be reasonable when provided")
    public boolean isPriceReasonableWhenProvided() {
        return price == null || price.compareTo(new BigDecimal("1000000")) < 0;
    }

    @AssertTrue(message = "Stock quantity must be reasonable when provided")
    public boolean isStockQuantityReasonableWhenProvided() {
        return stockQuantity == null || stockQuantity <= 100000;
    }
}
