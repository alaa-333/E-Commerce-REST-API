package com.e_commerce.E_Commerce.REST.API.mapper;

import com.e_commerce.E_Commerce.REST.API.dto.request.PaymentRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.PaymentUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.PaymentResponseDTO;
import com.e_commerce.E_Commerce.REST.API.model.Payment;
import com.e_commerce.E_Commerce.REST.API.model.Product;
import com.e_commerce.E_Commerce.REST.API.payment.PaymentMethod;
import com.e_commerce.E_Commerce.REST.API.payment.PaymentStatus;
import com.e_commerce.E_Commerce.REST.API.payment.PaymentStrategy;
import org.mapstruct.*;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Mapper(
        componentModel = "spring",
        imports = {LocalDateTime.class, StringUtils.class, DateTimeFormatter.class},
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PaymentMapper {

    // ============ Request to Entity ============

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "paymentGatewayResponse", ignore = true)
//    @Mapping(target = "order", ignore = true)
//    @Mapping(target = "paymentDate", ignore = true)
//    @Mapping(target = "paymentStatus", ignore = true)
//    @Mapping(target = "transactionId", ignore = true)
//    @Mapping(source = "paymentMethod", target = "paymentMethod", qualifiedByName = "stringToPaymentMethod")
//    @Mapping(source = "amount", target = "amount")
//    Payment toEntity(PaymentRequestDTO requestDTO);

    // ============ Entity to Response DTO ============

    @Mapping(target = "paymentMethod", source = "paymentMethod", qualifiedByName = "paymentMethodToString")
    @Mapping(target = "paymentStatus", source = "paymentStatus", qualifiedByName = "paymentStatusToString")
    @Mapping(target = "formattedAmount", expression = "java(formatAmount(payment.getAmount()))")
    @Mapping(target = "formattedPaymentDate", expression = "java(formatPaymentDate(payment.getPaymentDate()))")
    @Mapping(target = "maskedTransactionId", expression = "java(maskTransactionId(payment.getTransactionId()))")
    @Mapping(target = "isSuccessful", expression = "java(isPaymentSuccessful(payment.getPaymentStatus()))")
    @Mapping(target = "isRefundable", expression = "java(isPaymentRefundable(payment))")
    PaymentResponseDTO toResponseDTO(Payment payment);

    // ============ Update Entity ============

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "order", ignore = true)
//    @Mapping(target = "paymentMethod", ignore = true)
//    @Mapping(target = "amount", ignore = true)
//    @Mapping(target = "paymentDate", expression = "java(updatePaymentDate(payment, updateRequestDTO))")
//    void updateEntityFromDTO(PaymentUpdateRequestDTO updateRequestDTO, @MappingTarget Payment payment);

    // ============ Custom Mapping Methods ============

    @Named("stringToPaymentMethod")
    default PaymentMethod stringToPaymentMethod(String paymentMethod) {
        if (!StringUtils.hasText(paymentMethod)) {
            return null;
        }
        try {
            return PaymentMethod.valueOf(paymentMethod.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Named("paymentMethodToString")
    default String paymentMethodToString(PaymentMethod paymentMethod) {
        return paymentMethod != null ? paymentMethod.name() : null;
    }

    @Named("paymentStatusToString")
    default String paymentStatusToString(PaymentStatus paymentStatus) {
        return paymentStatus != null ? paymentStatus.name() : PaymentStatus.PENDING.name();
    }

    // ============ Helper Methods ============

    default String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return "$0.00";
        }
        return String.format("$%.2f", amount);
    }

    default String formatPaymentDate(LocalDateTime paymentDate) {
        if (paymentDate == null) {
            return "Not processed";
        }
        return paymentDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    default String maskTransactionId(String transactionId) {
        if (!StringUtils.hasText(transactionId) || transactionId.length() <= 8) {
            return transactionId;
        }
        return "***" + transactionId.substring(transactionId.length() - 8);
    }

    default boolean isPaymentSuccessful(PaymentStatus paymentStatus) {
        return paymentStatus == PaymentStatus.COMPLETED || paymentStatus == PaymentStatus.SUCCESSFUL;
    }

    default boolean isPaymentRefundable(Payment payment) {
        if (payment == null || payment.getPaymentStatus() == null || payment.getPaymentDate() == null) {
            return false;
        }

        boolean isSuccessful = isPaymentSuccessful(payment.getPaymentStatus());
        boolean withinRefundPeriod = payment.getPaymentDate()
                .isAfter(LocalDateTime.now().minusDays(30));

        return isSuccessful && withinRefundPeriod;
    }

    // ============ Payment Creation and Update Logic ============

    @AfterMapping
    default void setDefaultValues(@MappingTarget Payment payment, PaymentRequestDTO requestDTO) {
        if (payment != null) {
            payment.setPaymentStatus(PaymentStatus.PENDING);
        }
    }

    @AfterMapping
    default void updatePaymentDate(
            @MappingTarget Payment payment,
            PaymentUpdateRequestDTO updateRequestDTO) {

        if (payment == null || updateRequestDTO == null) {
            return;
        }

        // Update payment date when status changes to SUCCESSFUL/COMPLETED
        if (StringUtils.hasText(updateRequestDTO.getPaymentStatus())) {
            try {
                PaymentStatus newStatus = PaymentStatus.valueOf(
                        updateRequestDTO.getPaymentStatus().trim().toUpperCase()
                );

                boolean shouldSetPaymentDate = (newStatus == PaymentStatus.SUCCESSFUL ||
                        newStatus == PaymentStatus.COMPLETED) &&
                        payment.getPaymentDate() == null;

                if (shouldSetPaymentDate) {
                    payment.setPaymentDate(LocalDateTime.now());
                }
            } catch (IllegalArgumentException e) {
                // Invalid status, do nothing
            }
        }
    }

    default void updatePaymentFromDTO(PaymentUpdateRequestDTO requestDTO, Payment payment) {
        if (requestDTO == null || payment == null) {
            return;
        }

        // Update transactionId if provided
        Optional.ofNullable(requestDTO.getTransactionId())
                .filter(StringUtils::hasText)
                .ifPresent(payment::setTransactionId);

        // Update payment gateway response if provided
        Optional.ofNullable(requestDTO.getPaymentGatewayResponse())
                .filter(StringUtils::hasText)
                .ifPresent(payment::setPaymentGatewayResponse);

        // Update payment status if provided
        Optional.ofNullable(requestDTO.getPaymentStatus())
                .filter(StringUtils::hasText)
                .ifPresent(status -> {
                    try {
                        PaymentStatus paymentStatus = PaymentStatus.valueOf(status.trim().toUpperCase());
                        payment.setPaymentStatus(paymentStatus);

                        // Set payment date if status is successful and date is not set
                        if ((paymentStatus == PaymentStatus.SUCCESSFUL ||
                                paymentStatus == PaymentStatus.COMPLETED) &&
                                payment.getPaymentDate() == null) {
                            payment.setPaymentDate(LocalDateTime.now());
                        }
                    } catch (IllegalArgumentException e) {
                        // Invalid status, keep current status
                    }
                });
    }

//    default Payment createNewPayment(PaymentRequestDTO requestDTO) {
//        Payment payment = toEntity(requestDTO);
//        payment.setPaymentStatus(PaymentStatus.PENDING);
//        return payment;
//    }

    // ============ Utility Methods for Other Classes ============

    /**
     * Maps PaymentStatus enum to string for use in PaymentResult
     */
    default String mapPaymentStatusToString(PaymentStatus status) {
        return status != null ? status.name() : PaymentStatus.PENDING.name();
    }

    /**
     * Creates a PaymentResult from payment data
     */
    default PaymentStrategy.PaymentResult toPaymentResult(Payment payment) {
        if (payment == null) {
            return PaymentStrategy.PaymentResult.failure("Payment not found");
        }

        boolean isSuccessful = isPaymentSuccessful(payment.getPaymentStatus());

        if (isSuccessful) {
            return PaymentStrategy.PaymentResult.success(
                    payment.getTransactionId(),
                    generateClientSecret(payment) // You need to implement this based on your gateway
            );
        } else {
            return PaymentStrategy.PaymentResult.failure(
                    payment.getPaymentGatewayResponse() != null ?
                            payment.getPaymentGatewayResponse() : "Payment failed"
            );
        }
    }

    /**
     * Helper method to generate client secret (implement based on your payment gateway)
     */
    private String generateClientSecret(Payment payment) {
        // Implement based on your payment gateway (Stripe, PayPal, etc.)
        // Example: return "pi_" + payment.getId() + "_secret_" + UUID.randomUUID();
        return null;
    }
}