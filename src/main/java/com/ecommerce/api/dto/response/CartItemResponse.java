package com.ecommerce.api.dto.response;

import java.math.BigDecimal;
import java.util.List;
public record CartItemResponse(Long id, ProductResponse productResponse, Integer quantity, BigDecimal subtotal) {
}
