package com.e_commerce.E_Commerce.REST.API.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDTO {
    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Street is required")
    private String street;

    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "^[0-9A-Za-z\\-\\s]{3,10}$", message = "Invalid postal code format")
    private String postalCode;

    @NotBlank(message = "Country is required")
    private String country;
}
