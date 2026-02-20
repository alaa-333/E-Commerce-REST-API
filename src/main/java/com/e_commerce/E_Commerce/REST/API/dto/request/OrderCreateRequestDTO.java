package com.e_commerce.E_Commerce.REST.API.dto.request;

import com.e_commerce.E_Commerce.REST.API.exception.orderItem.OrderItemsEmptyException;
import com.e_commerce.E_Commerce.REST.API.exception.order.OrderTotalInvalidException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateRequestDTO {

    @NotNull(message = "Customer ID is required")
    @Positive(message = "Customer ID must be positive")
    private Long customerId;

    @NotNull(message = "Order items are required")
    private List<OrderItemCreateRequestDTO> orderItems;



}



