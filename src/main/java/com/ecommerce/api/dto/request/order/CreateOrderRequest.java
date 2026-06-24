package com.ecommerce.api.dto.request.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CreateOrderRequest  {

    @NotNull
    private ShippingAddress shippingAddress;

    @Size(max = 500)
    private String notes;

    @Getter
    @Setter
    public static class ShippingAddress {
        @NotNull
        private String city;
        @NotNull
        private String street;
        @NotNull
        private String postalCode;
        @NotNull
        private String country;
    }
}

