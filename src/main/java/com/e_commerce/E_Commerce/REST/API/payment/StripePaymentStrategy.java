package com.e_commerce.E_Commerce.REST.API.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Stripe implementation of PaymentStrategy.
 * Uses PaymentIntent API for secure payment processing.
 */
@Slf4j
@Component
public class StripePaymentStrategy implements PaymentStrategy {

    private static final String CURRENCY = "usd";

    @Override
    public PaymentResult processPayment(BigDecimal amount) {
        try {
            // Convert to cents (Stripe requires smallest currency unit)
            long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();

            // Build PaymentIntent parameters
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(CURRENCY)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build())
                    .build();

            // Create PaymentIntent via Stripe API
            PaymentIntent paymentIntent = PaymentIntent.create(params);

            log.info("Created Stripe PaymentIntent: {}", paymentIntent.getId());

            return PaymentResult.success(
                    paymentIntent.getId(),
                    paymentIntent.getClientSecret());

        } catch (StripeException e) {
            log.error("Stripe payment failed: {}", e.getMessage(), e);
            return PaymentResult.failure("Stripe error: " + e.getMessage());
        }
    }

    @Override
    public String getPaymentMethodType() {
        return PaymentMethod.STRIPE.toString();
    }
}
