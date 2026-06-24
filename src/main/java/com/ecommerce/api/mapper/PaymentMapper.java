package com.ecommerce.api.mapper;

import com.ecommerce.api.dto.response.PaymentResponse;
import com.ecommerce.api.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "paymentMethod", expression = "java(payment.getPaymentMethod().name())")
    @Mapping(target = "paymentStatus", expression = "java(payment.getPaymentStatus().name())")
    PaymentResponse toPaymentResponse(Payment payment);
}

