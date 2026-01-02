package com.e_commerce.E_Commerce.REST.API.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {

    STRIPE,
    PAYPAL
}
