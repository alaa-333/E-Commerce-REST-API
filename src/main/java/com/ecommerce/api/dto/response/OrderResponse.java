package com.ecommerce.api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        Long customerId,
        LocalDateTime orderDate,
        String status,
        BigDecimal totalAmount,
        List<OrderItemResponse> items,
        ShippingAddressResponse shippingAddress
){

}

