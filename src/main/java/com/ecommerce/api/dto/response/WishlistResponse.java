package com.ecommerce.api.dto.response;

import java.util.List;

public record WishlistResponse(
        Long id,
        Long customerId,
        List<WishlistItemResponse> items,
        Integer totalItems
) {
}

