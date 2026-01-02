package com.e_commerce.E_Commerce.REST.API.service;

import com.e_commerce.E_Commerce.REST.API.dto.request.PaymentRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.PaymentResponseDTO;
import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;
import com.e_commerce.E_Commerce.REST.API.exception.order.OrderNotFoundException;
import com.e_commerce.E_Commerce.REST.API.mapper.PaymentMapper;
import com.e_commerce.E_Commerce.REST.API.model.Order;
import com.e_commerce.E_Commerce.REST.API.model.Payment;
import com.e_commerce.E_Commerce.REST.API.payment.PaymentMethod;
import com.e_commerce.E_Commerce.REST.API.payment.PaymentStatus;
import com.e_commerce.E_Commerce.REST.API.payment.PaymentStrategy;
import com.e_commerce.E_Commerce.REST.API.payment.PaymentStrategyFactory;
import com.e_commerce.E_Commerce.REST.API.repository.OrderRepository;
import com.e_commerce.E_Commerce.REST.API.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PaymentService {

    private final PaymentStrategyFactory paymentStrategyFactory;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper mapper;

    /**
     * Creates a payment for an order using the specified payment method.
     *
     * @param requestDTO Payment request containing amount, payment method, and
     *                   order ID
     * @return PaymentResponseDTO with transaction details (including client_secret
     *         for Stripe)
     */
    public PaymentResponseDTO createPayment(PaymentRequestDTO requestDTO) {
        // 1. Validate payment request
        requestDTO.validatePaymentAmount();
        requestDTO.handlePaymentMethod();

        // 2. Verify order exists
        Order order = orderRepository.findById(requestDTO.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(requestDTO.getOrderId()));

        // 3. Check if payment method is supported
        String paymentMethod = requestDTO.getPaymentMethod();
        if (!paymentStrategyFactory.isSupported(paymentMethod)) {
            throw new ValidationException(ErrorCode.INVALID_PAYMENT_METHOD,
                    "Payment method '" + paymentMethod + "' is not supported.");
        }

        // 4. Get the appropriate payment strategy
        PaymentStrategy strategy = paymentStrategyFactory.getStrategy(paymentMethod);

        // 5. Process payment using the strategy
        PaymentStrategy.PaymentResult result = strategy.processPayment(requestDTO.getAmount());

        // 6. Create and save Payment entity
        Payment payment = Payment.builder()
                .paymentMethod(PaymentMethod.valueOf(paymentMethod))
                .amount(requestDTO.getAmount())
                .PaymentDate(LocalDateTime.now())
                .paymentStatus(result.success() ? PaymentStatus.PENDING : PaymentStatus.FAILED)
                .TransactionId(result.transactionId())
                .paymentGatewayResponse(result.clientSecret())
                .order(order)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        log.info("Payment created: id={}, transactionId={}, status={}",
                savedPayment.getId(), savedPayment.getTransactionId(), savedPayment.getPaymentStatus());

        // 7. Handle failure case
        if (!result.success()) {
            throw new ValidationException(ErrorCode.PAYMENT_FAILED, result.errorMessage());
        }

        // 8. Return response DTO
        return new PaymentResponseDTO(
                savedPayment.getId(),
                savedPayment.getPaymentMethod().toString(),
                savedPayment.getAmount(),
                null,
                savedPayment.getPaymentDate(),
                savedPayment.getPaymentStatus().name(),
                savedPayment.getTransactionId(),
                savedPayment.getPaymentGatewayResponse(),
                false,
                false,
                null,
                null);
    }

    /**
     * Retrieves a payment by its ID.
     */
    public PaymentResponseDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ValidationException(ErrorCode.PAYMENT_NOT_FOUND));

        return new PaymentResponseDTO(
                payment.getId(),
                payment.getPaymentMethod().toString(),
                payment.getAmount(),
                null,
                payment.getPaymentDate(),
                payment.getPaymentStatus().name(),
                payment.getTransactionId(),
                null, // Don't expose client secret on retrieval
                PaymentStatus.SUCCESSFUL.equals(payment.getPaymentStatus()),
                false,
                null,
                null);
    }

    /**
     * Updates payment status (e.g., after webhook confirmation from Stripe).
     */
    public void updatePaymentStatus(String transactionId, PaymentStatus status) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ValidationException(ErrorCode.PAYMENT_NOT_FOUND));

        payment.setPaymentStatus(status);
        paymentRepository.save(payment);

        log.info("Payment status updated: transactionId={}, newStatus={}", transactionId, status);
    }
}
