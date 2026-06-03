package com.ecommerce.api.dto.response;

import java.time.LocalDateTime;

public record CategoryResponse(long id, String name, String description, boolean active, int productCount, LocalDateTime createdAt) {
}
