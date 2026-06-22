package com.ecommerce.api.dto.response;

import java.time.LocalDateTime;

public record WishlistItemResponse(
        Long id,
        ProductResponse product,
        LocalDateTime addedAt
) {
}

