package com.e_commerce.E_Commerce.REST.API.payment;

import java.math.BigDecimal;

/**
 * Strategy interface for processing payments.
 * Each payment method (Stripe, PayPal, etc.) implements this interface.
 */
public interface PaymentStrategy {

    /**
     * Processes a payment for the given amount.
     *
     * @param amount The amount to charge (in standard currency units, e.g., 100.50
     *               for $100.50)
     * @return PaymentResult containing transaction details
     */
    PaymentResult processPayment(BigDecimal amount);

    /**
     * Returns the payment method identifier (e.g., "STRIPE", "PAYPAL").
     */
    String getPaymentMethodType();

    /**
     * Result object returned after processing a payment.
     */
    record PaymentResult(
            boolean success,
            String transactionId,
            String clientSecret,
            String errorMessage) {
        public static PaymentResult success(String transactionId, String clientSecret) {
            return new PaymentResult(true, transactionId, clientSecret, null);
        }

        public static PaymentResult failure(String errorMessage) {
            return new PaymentResult(false, null, null, errorMessage);
        }
    }
}
