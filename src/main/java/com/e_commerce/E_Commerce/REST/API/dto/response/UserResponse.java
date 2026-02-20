package com.e_commerce.E_Commerce.REST.API.dto.response;

import com.e_commerce.E_Commerce.REST.API.model.enums.Role;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;
@Builder
public record UserResponse(
        Long id,
        String email,
        Set<Role> roles,
        boolean enabled,
        boolean accountNonLocked,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
