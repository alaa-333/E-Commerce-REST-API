package com.e_commerce.E_Commerce.REST.API.mapper;

import com.e_commerce.E_Commerce.REST.API.dto.request.PaymentRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.PaymentUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.PaymentResponseDTO;
import com.e_commerce.E_Commerce.REST.API.model.Payment;
import com.e_commerce.E_Commerce.REST.API.model.Product;
import com.e_commerce.E_Commerce.REST.API.model.enums.PaymentStatus;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;

public interface PaymentMapper {

    // request to entity ===
    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "paymentDate" , ignore = true)
    @Mapping(target = "transactionId" , ignore = true)
    @Mapping(target = "paymentGatewayResponse" , ignore = true)
    @Mapping(target = "order" , ignore = true)
    Payment toEntity(PaymentRequestDTO requestDTO);

    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "paymentMethod" , ignore = true)
    @Mapping(target = "amount" , ignore = true)
    @Mapping(target = "paymentDate", ignore = true)
    @Mapping(target = "order" , ignore = true)
    void updateEntityFromDTO(PaymentUpdateRequestDTO updateRequestDTO , @MappingTarget Product product);


    // ----- entity to responseDTO

    @Mapping(source = "order" , target = "order")
    PaymentResponseDTO toResponseDTO (Payment payment);

    // fun to handle partial updates

    default void updatePaymentFromDTO(PaymentUpdateRequestDTO requestDTO , Payment payment) {
        if (requestDTO == null || payment == null)
            return;

        if (requestDTO.hasPaymentStatus()) {
            PaymentStatus paymentStatus = PaymentStatus.valueOf(requestDTO.getPaymentStatus().trim().toUpperCase());
            payment.setPaymentStatus(paymentStatus);

            if (paymentStatus == PaymentStatus.SUCCESSFUL && payment.getPaymentDate() == null) {
                payment.setPaymentDate(LocalDateTime.now());
            }


            if (requestDTO.hasTransactionId()) {
                payment.setTransactionId(requestDTO.getTransactionId());
            }

            if (requestDTO.hasPaymentGatewayResponse()) {
                payment.setPaymentGatewayResponse(requestDTO.getPaymentGatewayResponse());
            }
        }



    }
    default Payment createNewPayment(PaymentRequestDTO requestDTO) {
        Payment payment = toEntity(requestDTO);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        return payment;
    }

    default String mapPaymentStatusToString(PaymentStatus status)
    {
        return status != null  ?   status.name():"PENDING";
    }

}
