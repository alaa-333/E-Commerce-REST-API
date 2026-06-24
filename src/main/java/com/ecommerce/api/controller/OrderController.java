package com.ecommerce.api.controller;

import com.ecommerce.api.dto.request.order.CreateOrderRequest;
import com.ecommerce.api.dto.request.order.StatusRequest;
import com.ecommerce.api.dto.response.ApiResponse;
import com.ecommerce.api.dto.response.OrderResponse;
import com.ecommerce.api.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        var resp = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(resp));
    }

    @GetMapping
    // admin only: get all orders with optional status filter
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status
    ) {
        var resp = orderService.getAll(page, size, status);
        return ResponseEntity.ok(ApiResponse.success(resp));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var resp = orderService.getMyOrders(page, size);
        return ResponseEntity.ok(ApiResponse.success(resp));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable Long id) {
        var resp = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(resp));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        var resp = orderService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success(resp));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable Long id) {
        var resp = orderService.cancelOrder(id);
        return ResponseEntity.ok(ApiResponse.success(resp));
    }


}


