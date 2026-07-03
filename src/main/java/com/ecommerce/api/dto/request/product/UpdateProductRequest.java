package com.ecommerce.api.dto.request.product;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class UpdateProductRequest {
    String name;
    String description;
    BigDecimal price;
    Integer stockQuantity;
    Long categoryId;
    Boolean active;

}
