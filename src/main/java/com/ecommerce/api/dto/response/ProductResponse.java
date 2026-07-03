package com.ecommerce.api.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ProductResponse(Long id,
                              String name,
                              String description,
                              BigDecimal price,
                              Integer stockQuantity,
                              CategoryResponse category,
                              String imageUrl,
                              Boolean active,
                              LocalDateTime createdAt) {
}
