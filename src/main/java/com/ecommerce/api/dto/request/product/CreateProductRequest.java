package com.ecommerce.api.dto.request.product;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    @Size(min = 10, max = 1000, message = "Product description must be between 10 and 1000 characters")
    @NotBlank(message = "Product description is required")
    private String description;

    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;

    @NotNull(message = "Product stock quantity is required")
    @PositiveOrZero(message = "Stock quantity must be zero or a positive integer")
    private Integer stockQuantity;

    @NotBlank(message = "Product category is required")
    @Positive(message = "Category ID must be a positive integer")
    private Long categoryId;

    @NotBlank(message = "Product image URL is required")
    private String imageUrl;
}
