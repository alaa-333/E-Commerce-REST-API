package com.ecommerce.api.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CategoryResponse(long id, String name, String description, boolean active, int productCount, LocalDateTime createdAt) {
}
