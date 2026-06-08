package com.ecommerce.api.dto.request.product;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class UpdateProductRequest {
    String name;
    String description;
    BigDecimal price;
    Integer stockQuantity;
    Long categoryId;
    Boolean active;

}
