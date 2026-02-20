package com.e_commerce.E_Commerce.REST.API.payment;

import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory for selecting the appropriate PaymentStrategy based on payment
 * method.
 * Uses Spring's dependency injection to automatically discover all
 * PaymentStrategy implementations.
 */
@Component
public class PaymentStrategyFactory {

    private final Map<String, PaymentStrategy> strategyMap;

    /**
     * Constructor injection - Spring automatically injects all PaymentStrategy
     * beans.
     */
    public PaymentStrategyFactory(List<PaymentStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        PaymentStrategy::getPaymentMethodType,
                        Function.identity()));
    }

    /**
     * Gets the appropriate payment strategy for the given payment method.
     *
     * @param paymentMethod The payment method type (e.g., "STRIPE", "PAYPAL")
     * @return The corresponding PaymentStrategy
     * @throws IllegalArgumentException if no strategy exists for the payment method
     */
    public PaymentStrategy getStrategy(String paymentMethod) {
        PaymentStrategy strategy = strategyMap.get(paymentMethod.toUpperCase());
        if (strategy == null) {
            throw new ValidationException(
                    ErrorCode.INVALID_PAYMENT_METHOD,
                    String.format("supported payments method %s",strategyMap.keySet())
            );
        }
        return strategy;
    }

    /**
     * Checks if a payment method is supported.
     */
    public boolean isSupported(String paymentMethod) {
        return strategyMap.containsKey(paymentMethod.toUpperCase());
    }
}
