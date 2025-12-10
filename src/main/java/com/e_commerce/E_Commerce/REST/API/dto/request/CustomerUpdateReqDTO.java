package com.e_commerce.E_Commerce.REST.API.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerUpdateReqDTO {


    @Pattern(regexp = "^[a-zA-Z ]{2,50}$", message = "First name must be 2-50 alphabetic characters")
    private String firstName;

    @Pattern(regexp = "^[a-zA-Z ]{2,50}$", message = "Last name must be 2-50 alphabetic characters")
    private String lastName;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^\\+?[0-9\\-\\s()]{10,15}$", message = "Invalid phone number format")
    private String phone;

    private AddressRequestDTO address;




}
