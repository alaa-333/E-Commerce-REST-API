package com.e_commerce.E_Commerce.REST.API.dto.request;

import jakarta.validation.constraints.Email;
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
public class CustomerCreateRequestDTO {

    @NotBlank(message = "First name is required")
    @Pattern(regexp = "^[a-zA-Z ]{2,50}$", message = "First name must be 2-50 alphabetic characters")
    private String firstName;

    @NotBlank(message = "last name is required")
    @Pattern(regexp = "^[a-zA-Z ]{2,50}$", message = "First name must be 2-50 alphabetic characters")
    private String LastName;

    @NotBlank(message = "email is required")
    @Email(message = "invalid email format")
    private String email;

    @NotBlank(message = "phone number is required")
    @Pattern(regexp = "^\\+?[0-9\\-\\s()]{10,15}$", message = "Invalid phone number format")
    private String phone;

    @NotNull(message = "address is required")
    private AddressRequestDTO address;

    // custom helper validation methods
    public void removeSpaces()
    {
        if (this.firstName != null)
        {
            this.firstName = this.firstName.trim();
        }
        if (this.LastName != null)
        {
            this.LastName = this.LastName.trim();
        }
    }
    public void handlingEmail()
    {
        if (this.email != null)
        {
            this.email = email.trim().toLowerCase();
        }
    }


}
