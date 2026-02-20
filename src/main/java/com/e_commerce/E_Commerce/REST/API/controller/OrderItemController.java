package com.e_commerce.E_Commerce.REST.API.controller;

import com.e_commerce.E_Commerce.REST.API.dto.request.OrderItemCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.OrderItemUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.OrderItemResponseDTO;
import com.e_commerce.E_Commerce.REST.API.service.OrderItemService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/orders/{orderId}/items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping
    public ResponseEntity<List<OrderItemResponseDTO>> getAll(
            @PathVariable
            @NotNull(message = "Order ID cannot be null")
            @Positive(message = "Order ID must be positive")
            Long orderId,
            @RequestParam(value = "productId", required = false) Long productId
    ) {
        List<OrderItemResponseDTO> orderItems;
        if (productId != null) {
            orderItems = orderItemService.getItemsByOrderAndProduct(orderId, productId);
        } else {
            orderItems = orderItemService.getOrderItemsByOrder(orderId);
        }

        if (orderItems.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(orderItems);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<OrderItemResponseDTO> getOrderItem(
            @PathVariable("orderId")
            @NotNull(message = "Order ID cannot be null")
            @Positive(message = "Order ID must be positive")
            Long orderId,
            @PathVariable("itemId")
            @NotNull(message = "Item ID cannot be null")
            @Positive(message = "Item ID must be positive")
            Long itemId
    ) {
        OrderItemResponseDTO responseDTO = orderItemService.getOrderItemById(orderId, itemId);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    public ResponseEntity<OrderItemResponseDTO> addItem(
            @PathVariable("orderId") 
            @Positive 
            @NotNull 
            Long orderId,
            @Valid @RequestBody OrderItemCreateRequestDTO requestDTO
    ) {
        OrderItemResponseDTO responseDTO = orderItemService.addOrderItem(orderId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<OrderItemResponseDTO> updateQuantity(
            @PathVariable("orderId")
            @NotNull(message = "Order ID cannot be null")
            @Positive(message = "Order ID must be positive")
            Long orderId,
            @PathVariable("itemId")
            @NotNull(message = "Item ID cannot be null")
            @Positive(message = "Item ID must be positive")
            Long itemId,
            @Valid @RequestBody OrderItemUpdateRequestDTO requestDTO
    ) {
        OrderItemResponseDTO responseDTO = orderItemService.updateOrderItemQuantity(
                orderId, itemId, requestDTO
        );
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable
            @NotNull(message = "Order ID cannot be null")
            @Positive(message = "Order ID must be positive") 
            Long orderId,
            @PathVariable
            @NotNull(message = "Item ID cannot be null")
            @Positive(message = "Item ID must be positive") 
            Long itemId
    ) {
        orderItemService.deleteOrderItem(orderId, itemId);
        return ResponseEntity.noContent().build();
    }
}
