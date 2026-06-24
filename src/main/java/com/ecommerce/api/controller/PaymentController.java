package com.ecommerce.api.controller;

import com.stripe.exception.StripeException;
import com.ecommerce.api.dto.request.payment.CreatePaymentRequest;
import com.ecommerce.api.dto.response.ApiResponse;
import com.ecommerce.api.dto.response.PaymentResponse;
import com.ecommerce.api.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(@RequestBody @Valid CreatePaymentRequest request) throws StripeException {
        var response = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(@PathVariable Long id) {
        var response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrderId(@PathVariable Long orderId) {
        var response = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

