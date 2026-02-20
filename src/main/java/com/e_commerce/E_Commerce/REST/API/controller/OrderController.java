package com.e_commerce.E_Commerce.REST.API.controller;

import com.e_commerce.E_Commerce.REST.API.dto.request.OrderCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.OrderUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.PaginationRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.response.OrderResponseDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.PaginationResponseDto;
import com.e_commerce.E_Commerce.REST.API.service.OrderService;
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
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderCreateRequestDTO requestDTO)
    {
        OrderResponseDTO orderResponseDTO = orderService.createOrder(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable
            @NotNull(message = "Order ID cannot be null")
            @Positive(message = "Order ID must be positive")
            Long id,
            @Valid @RequestBody OrderUpdateRequestDTO requestDTO)
    {
        OrderResponseDTO responseDTO = orderService.updateOrderStatus(id,requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @PathVariable
            @NotNull(message = "Order ID cannot be null")
            @Positive(message = "Order ID must be positive")
            Long id)
    {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping
    public ResponseEntity<PaginationResponseDto<OrderResponseDTO>> getAll(
            @Valid PaginationRequestDto requestDto
    )
    {
        PaginationResponseDto<OrderResponseDTO> responseDTOS = orderService.getAll(requestDto);
        return ResponseEntity.ok(responseDTOS);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrdersByCustomer(
            @PathVariable
            @NotNull(message = "Customer ID cannot be null")
            @Positive(message = "Customer ID must be positive")
            Long customerId)
    {
        List<OrderResponseDTO> responseDTOS = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(responseDTOS);
    }

    @GetMapping("/status")
    public ResponseEntity<PaginationResponseDto<OrderResponseDTO>> getOrdersByStatus(
            @RequestParam
            @NotNull(message = "Status cannot be null")
            String status,
            @Valid PaginationRequestDto requestDto)
    {
         return ResponseEntity.ok(orderService.getOrderByStatus(status ,requestDto));
    }
}
