package com.ecommerce.api.dto.request.wishlist;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AddToWishlistRequest {

    @NotNull(message = "Product ID is required")
    @Min(value = 1, message = "Product ID must be a positive number")
    private Long productId;
}

