package com.e_commerce.E_Commerce.REST.API.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequestDTO {

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Product description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have up to 10 integer digits and 2 fraction digits")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @NotBlank(message = "Category is required")
    @Size(min = 2, max = 50, message = "Category must be between 2 and 50 characters")
    private String category;

    @NotBlank(message = "Image URL is required")
    @Pattern(regexp = "^(https?://.*\\.(?:png|jpg|jpeg|gif|webp))$",
            message = "Image URL must be a valid URL ending with .png, .jpg, .jpeg, .gif, or .webp")
    private String imgUrl;

    // Custom validation methods
    public void sanitizeInput() {
        if (this.name != null) {
            this.name = this.name.trim();
        }
        if (this.description != null) {
            this.description = this.description.trim();
        }
        if (this.category != null) {
            this.category = this.category.trim();
        }
        if (this.imgUrl != null) {
            this.imgUrl = this.imgUrl.trim();
        }
    }

    public boolean isPriceReasonable() {
        return price != null && price.compareTo(new BigDecimal("1000000")) < 0; // Max 1 million
    }

    public void validateBusinessRules() {
        if (stockQuantity != null && stockQuantity > 100000) {
            throw new IllegalArgumentException("Stock quantity cannot exceed 100,000 units");
        }
    }
}
