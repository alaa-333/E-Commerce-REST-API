package com.ecommerce.api.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank(message = "email can be not empty")
    @Email(message = "invalid email format")
    private String email;

    @NotBlank(message = "password can be not empty")
    @Size(min = 8, max = 100)
    private String password;
}
