package com.ecommerce.api.dto.request.product;

import java.math.BigDecimal;

public record ProductSearchCriteria(
        String keyword,
        Long categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {
}
