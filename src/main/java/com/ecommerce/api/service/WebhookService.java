package com.ecommerce.api.service;

import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.ecommerce.api.entity.Order;
import com.ecommerce.api.entity.Payment;
import com.ecommerce.api.entity.enums.OrderStatus;
import com.ecommerce.api.entity.enums.PaymentStatus;
import com.ecommerce.api.repository.OrderRepository;
import com.ecommerce.api.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void processStripeEvent(Event event) {
        log.info("Processing Stripe event: type={}, id={}", event.getType(), event.getId());

        try {
            switch (event.getType()) {
                case "payment_intent.succeeded":
                    handlePaymentSucceeded(event);
                    break;
                case "payment_intent.payment_failed":
                    handlePaymentFailed(event);
                    break;
                default:
                    log.debug("Unhandled Stripe event type: {}", event.getType());
            }
        } catch (Exception e) {
            log.error("Error processing Stripe event", e);
            // We still return 200 to Stripe to prevent retries
            // Idempotency prevents duplicate processing
        }
    }

    @Transactional
    protected void handlePaymentSucceeded(Event event) {
        PaymentIntent paymentIntent = extractPaymentIntent(event);

        log.info("Payment succeeded for PaymentIntent: {}", paymentIntent.getId());

        // Find payment by transaction ID
        Optional<Payment> paymentOpt = paymentRepository.findByTransactionId(paymentIntent.getId());
        if (paymentOpt.isEmpty()) {
            log.warn("Payment not found for PaymentIntent ID: {}", paymentIntent.getId());
            return;
        }

        Payment payment = paymentOpt.get();

        // Only update if still pending (idempotency)
        if (payment.getPaymentStatus() == PaymentStatus.PENDING) {
            payment.setPaymentStatus(PaymentStatus.COMPLETED);
            payment.setPaymentDate(LocalDateTime.now());
            paymentRepository.save(payment);

            log.info("Payment status updated to COMPLETED for order: {}", payment.getOrderId());

            // Update order status to CONFIRMED
            Optional<Order> orderOpt = orderRepository.findById(payment.getOrderId());
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                if (order.getOrderStatus() == OrderStatus.PENDING) {
                    order.setOrderStatus(OrderStatus.CONFIRMED);
                    orderRepository.save(order);
                    log.info("Order status updated to CONFIRMED: {}", order.getId());
                }
            }
        }
    }

    @Transactional
    protected void handlePaymentFailed(Event event) {
        PaymentIntent paymentIntent = extractPaymentIntent(event);

        log.info("Payment failed for PaymentIntent: {}", paymentIntent.getId());

        // Find payment by transaction ID
        Optional<Payment> paymentOpt = paymentRepository.findByTransactionId(paymentIntent.getId());
        if (paymentOpt.isEmpty()) {
            log.warn("Payment not found for PaymentIntent ID: {}", paymentIntent.getId());
            return;
        }

        Payment payment = paymentOpt.get();

        // Only update if still pending (idempotency)
        if (payment.getPaymentStatus() == PaymentStatus.PENDING) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setGatewayResponse(paymentIntent.getLastPaymentError() != null ? paymentIntent.getLastPaymentError().getMessage() : "Unknown error");
            paymentRepository.save(payment);

            log.info("Payment status updated to FAILED for order: {}", payment.getOrderId());
        }
    }

    private PaymentIntent extractPaymentIntent(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        PaymentIntent paymentIntent = (PaymentIntent) deserializer.getObject()
                .orElseThrow(() -> new RuntimeException("Unable to deserialize event data"));
        return paymentIntent;
    }
}

