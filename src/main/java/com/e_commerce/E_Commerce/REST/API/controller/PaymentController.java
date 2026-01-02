package com.e_commerce.E_Commerce.REST.API.controller;

import com.e_commerce.E_Commerce.REST.API.dto.request.PaymentRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.PaymentResponseDTO;
import com.e_commerce.E_Commerce.REST.API.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Creates a new payment for an order.
     * Returns client_secret (in paymentGatewayResponse) for frontend to confirm
     * payment.
     */
    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(@Valid @RequestBody PaymentRequestDTO requestDTO) {
        PaymentResponseDTO response = paymentService.createPayment(requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a payment by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long id) {
        PaymentResponseDTO response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(response);
    }
}
