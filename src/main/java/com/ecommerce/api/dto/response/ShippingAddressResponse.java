package com.ecommerce.api.dto.response;

public record ShippingAddressResponse(
        String city,
        String street,
        String postalCode,
        String country
){

}

