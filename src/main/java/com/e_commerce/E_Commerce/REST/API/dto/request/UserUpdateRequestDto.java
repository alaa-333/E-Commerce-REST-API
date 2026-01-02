package com.e_commerce.E_Commerce.REST.API.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequestDto {

    @Email(message = "invalid email format")
    private String email;

    @Size(min = 8 , max = 20 , message = "password length invalid")
    private String password;

    private Boolean enabled;

    private Boolean accountNonLocked;

}

