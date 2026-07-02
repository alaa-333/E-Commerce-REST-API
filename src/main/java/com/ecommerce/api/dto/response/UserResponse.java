package com.ecommerce.api.dto.response;


import com.ecommerce.api.entity.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


public record UserResponse(

        long id,
        String email,
        Set<Role> roles,
        LocalDateTime createdAt
) {
}
