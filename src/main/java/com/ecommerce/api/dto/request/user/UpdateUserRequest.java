package com.ecommerce.api.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {

    @NotBlank(message = "email must be not blank")
    @Email(message = "email must be a valid email address")
    private String email;

    @NotBlank(message = "current password must be not blank")
    @Size(min = 8, max = 100, message = "current password must be at least 8 characters long")
    private String currentPassword;

    @NotBlank(message = "new password must be not blank")
    @Size(min = 8, max = 100, message = "new password must be at least 8 characters long")
    private String newPassword;
}
