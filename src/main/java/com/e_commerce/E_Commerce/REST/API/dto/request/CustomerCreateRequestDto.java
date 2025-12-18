package com.e_commerce.E_Commerce.REST.API.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerCreateRequestDto {

    @NotBlank(message = "First name is required")
    @Pattern(regexp = "^[a-zA-Z ]{2,50}$", message = "First name must be 2-50 alphabetic characters")
    private String firstName;

    @NotBlank(message = "last name is required")
    @Pattern(regexp = "^[a-zA-Z ]{2,50}$", message = "First name must be 2-50 alphabetic characters")
    private String lastName;



    @NotBlank(message = "phone number is required")
    @Pattern(regexp = "^\\+?[0-9\\-\\s()]{10,15}$", message = "Invalid phone number format")
    private String phone;

    @NotNull(message = "address is required")
    @Valid
    private AddressRequestDTO address;







}
