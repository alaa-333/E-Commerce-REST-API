package com.e_commerce.E_Commerce.REST.API.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentUpdateRequestDTO {


        @Pattern(regexp = "^(PENDING|PROCESSING|COMPLETED|FAILED|REFUNDED|CANCELLED)?$",
                message = "Status must be one of: PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED, CANCELLED")
        private String paymentStatus;

        @Size(max = 100, message = "Transaction ID cannot exceed 100 characters")
        private String transactionId;

        private String paymentGatewayResponse;

        // Helper methods for partial updates
        public boolean hasPaymentStatus() {
            return paymentStatus != null && !paymentStatus.isBlank();
        }

        public boolean hasTransactionId() {
            return transactionId != null && !transactionId.isBlank();
        }

        public boolean hasPaymentGatewayResponse() {
            return paymentGatewayResponse != null && !paymentGatewayResponse.isBlank();
        }

        // Conditional validation
        @AssertTrue(message = "Transaction ID is required when status is COMPLETED")
        public boolean isTransactionIdRequiredForCompletedStatus() {
            if ("COMPLETED".equals(paymentStatus)) {
                return transactionId != null && !transactionId.isBlank();
            }
            return true;
        }
    }
