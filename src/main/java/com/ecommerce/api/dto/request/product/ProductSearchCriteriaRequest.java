package com.ecommerce.api.dto.request.product;

import lombok.Getter;

import java.math.BigDecimal;

public record ProductSearchCriteriaRequest (

        String keyword,
        Long categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice

) {
}
