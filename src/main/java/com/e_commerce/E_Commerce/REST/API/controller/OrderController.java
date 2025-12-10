package com.e_commerce.E_Commerce.REST.API.controller;

import com.e_commerce.E_Commerce.REST.API.dto.request.OrderCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.OrderUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.PaginationRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.response.OrderResponseDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.PaginationResponseDto;
import com.e_commerce.E_Commerce.REST.API.repository.OrderRepository;
import com.e_commerce.E_Commerce.REST.API.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

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
            @NotNull(message = "id can be not null")
            @Positive(message = "id must be positive value")
            Long id,
            @Valid @RequestBody OrderUpdateRequestDTO requestDTO)
    {
        OrderResponseDTO responseDTO = orderService.updateOrderStatus(id,requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @PathVariable
            @NotNull (message = "id can be not null")
            @Positive(message = "id must be positive value")
            Long id)
    {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }


    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAll()
    {
        List<OrderResponseDTO> responseDTOS = orderService.getAll();
        return ResponseEntity.ok(responseDTOS);
    }


    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrdersByCustomer(
            @PathVariable
            @NotNull (message = "id can be not null")
            @Positive(message = "id must be positive value")
            Long customerId)
    {
        List<OrderResponseDTO> responseDTOS = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(responseDTOS);
    }

    @GetMapping("/status")
    public ResponseEntity<PaginationResponseDto<OrderResponseDTO>> getOrdersByStatus(

            @RequestParam
            @NotNull(message = "status can be not null")
            String status
            , @Valid PaginationRequestDto requestDto)
    {
         return ResponseEntity.ok(orderService.getOrderByStatus(status ,requestDto));
    }



}
