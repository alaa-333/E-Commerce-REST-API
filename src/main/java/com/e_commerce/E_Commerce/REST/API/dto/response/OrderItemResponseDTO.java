package com.e_commerce.E_Commerce.REST.API.dto.response;

import java.math.BigDecimal;

public record OrderItemResponseDTO
        (
                Long id,
                Long productId,
                String productName,
                Integer quantity,
                BigDecimal unitPrice,
                BigDecimal itemTotal,
                String imageUrl
        ) {

    public OrderItemResponseDTO {
        // Calculate derived field
        if (itemTotal == null && quantity != null && unitPrice != null) {
            itemTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }

        // Default values
        if (imageUrl == null) {
            imageUrl = "/images/default-product.png";
        }
    }

}
