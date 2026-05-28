package com.ecommerce.api.entity.enums;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public enum Role {
    ROLE_ADMIN,
    ROLE_USER
}
