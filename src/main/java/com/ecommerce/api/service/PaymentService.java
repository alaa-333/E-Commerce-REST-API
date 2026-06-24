package com.ecommerce.api.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.ecommerce.api.dto.request.payment.CreatePaymentRequest;
import com.ecommerce.api.dto.response.PaymentResponse;
import com.ecommerce.api.entity.Order;
import com.ecommerce.api.entity.Payment;
import com.ecommerce.api.entity.enums.PaymentMethod;
import com.ecommerce.api.entity.enums.PaymentStatus;
import com.ecommerce.api.entity.enums.OrderStatus;
import com.ecommerce.api.exception.EcommerceAppException;
import com.ecommerce.api.exception.ErrorCode;
import com.ecommerce.api.exception.ResourceNotFoundException;
import com.ecommerce.api.mapper.PaymentMapper;
import com.ecommerce.api.repository.OrderRepository;
import com.ecommerce.api.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.api.entity.User;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Value("${stripe.secret-key:#{null}}")
    private String stripeSecretKey;

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public PaymentResponse createPayment(CreatePaymentRequest request) throws StripeException {
        var customer = getCurrentCustomer();

        // Validate order exists and belongs to customer
        var order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ORDER_NOT_FOUND, "order not found with id " + request.getOrderId()));

        // Check ownership
        if (order.getCustomer() == null || !order.getCustomer().getId().equals(customer.getId())) {
            throw new EcommerceAppException(ErrorCode.ACCESS_DENIED);
        }

        // Only allow payment creation for PENDING orders
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new EcommerceAppException(ErrorCode.VALIDATION_FAILED, "order is not in PENDING status");
        }

        // Check if payment already exists for this order
        if (paymentRepository.findByOrderId(request.getOrderId()).isPresent()) {
            throw new EcommerceAppException(ErrorCode.VALIDATION_FAILED, "payment already exists for this order");
        }

        // Convert amount to cents for Stripe
        long amountInCents = order.getTotalAmount().movePointRight(2).longValue();

        // Create Stripe PaymentIntent
        log.info("Creating PaymentIntent for order: {}, amount: {} cents", order.getId(), amountInCents);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("usd")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .putMetadata("order_id", order.getId().toString())
                .putMetadata("customer_id", customer.getId().toString())
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        log.info("PaymentIntent created: {} with status: {}", paymentIntent.getId(), paymentIntent.getStatus());

        // Persist payment record
        var payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(PaymentMethod.STRIPE);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setTransactionId(paymentIntent.getId());
        payment.setPaymentDate(java.time.LocalDateTime.now());

        paymentRepository.save(payment);

        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                paymentIntent.getPaymentMethod() != null ? paymentIntent.getPaymentMethod() : "STRIPE",
                payment.getPaymentStatus().name(),
                paymentIntent.getClientSecret(),
                paymentIntent.getId(),
                payment.getPaymentDate()
        );
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public PaymentResponse getPaymentById(Long id) {
        var payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.VALIDATION_FAILED, "payment not found with id " + id));

        var customer = getCurrentCustomer();
        var order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ORDER_NOT_FOUND, "order not found"));

        var auth = SecurityContextHolder.getContext().getAuthentication();
        var isAdmin = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !order.getCustomer().getId().equals(customer.getId())) {
            throw new EcommerceAppException(ErrorCode.ACCESS_DENIED);
        }

        return paymentMapper.toPaymentResponse(payment);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        var payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.VALIDATION_FAILED, "payment not found for order " + orderId));

        var customer = getCurrentCustomer();
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ORDER_NOT_FOUND, "order not found"));

        var auth = SecurityContextHolder.getContext().getAuthentication();
        var isAdmin = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !order.getCustomer().getId().equals(customer.getId())) {
            throw new EcommerceAppException(ErrorCode.ACCESS_DENIED);
        }

        return paymentMapper.toPaymentResponse(payment);
    }

    private com.ecommerce.api.entity.Customer getCurrentCustomer() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResourceNotFoundException(ErrorCode.ACCESS_DENIED, "user not authenticated");
        }
        var principal = auth.getPrincipal();
        if (!(principal instanceof User user) || user.getCustomer() == null) {
            throw new ResourceNotFoundException(ErrorCode.ACCESS_DENIED, "authenticated principal has no customer");
        }
        return user.getCustomer();
    }
}


