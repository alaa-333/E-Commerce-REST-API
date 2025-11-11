package com.e_commerce.E_Commerce.REST.API.dto.response;

public record AddressResponseDTO
        (
                String city,
                String street,
                String postalCode,
                String country
        )
{

    public String getFormattedAddress() {
        return String.format("%s, %s, %s, %s", street, city, postalCode, country);
    }
}
