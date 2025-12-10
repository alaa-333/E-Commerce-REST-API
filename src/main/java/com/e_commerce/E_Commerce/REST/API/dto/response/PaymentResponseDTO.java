package com.e_commerce.E_Commerce.REST.API.dto.response;

import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponseDTO(
        Long id,
        String paymentMethod,
        BigDecimal amount,
        String formattedAmount,
        LocalDateTime paymentDate,
        String paymentStatus,
        String transactionId,
        String paymentGatewayResponse,
        boolean isSuccessful,
        boolean isRefundable,
        String formattedPaymentDate,
        String maskedTransactionId
) {
    public PaymentResponseDTO {
        // Validation
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException(ErrorCode.INVALID_PRICE);
        }

        // Default values
        if (paymentStatus == null) {
            paymentStatus = "PENDING";
        }

        // Derived data
        formattedAmount = String.format("$%.2f", amount);
        isSuccessful = "COMPLETED".equals(paymentStatus);
        isRefundable = isSuccessful && paymentDate != null &&
                paymentDate.isAfter(LocalDateTime.now().minusDays(30));
        formattedPaymentDate = paymentDate != null ?
                paymentDate.toString() : "Not processed";
        maskedTransactionId = maskTransactionId(transactionId);
    }

    private static String maskTransactionId(String transactionId) {
        if (transactionId == null || transactionId.length() <= 8) {
            return transactionId;
        }
        return "***" + transactionId.substring(transactionId.length() - 8);
    }

    // Business logic methods
    public boolean isPending() {
        return "PENDING".equals(paymentStatus);
    }

    public boolean isFailed() {
        return "FAILED".equals(paymentStatus);
    }

    public boolean canRetry() {
        return isFailed() || isPending();
    }

    public String getStatusDescription() {
        return switch (paymentStatus) {
            case "COMPLETED" -> "Payment completed successfully";
            case "PENDING" -> "Payment is pending processing";
            case "PROCESSING" -> "Payment is being processed";
            case "FAILED" -> "Payment failed. Please try again";
            case "REFUNDED" -> "Payment has been refunded";
            case "CANCELLED" -> "Payment was cancelled";
            default -> "Unknown payment status";
        };
    }
}