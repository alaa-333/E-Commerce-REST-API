package com.ecommerce.api.dto.request.product;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductSearchCriteria(
        String keyword,
        Long categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {
}
