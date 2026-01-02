package com.e_commerce.E_Commerce.REST.API.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Getter
@AllArgsConstructor
public enum Role  implements GrantedAuthority {

    ROLE_ADMIN,
    ROLE_USER;



    @Override
    public String getAuthority() {
        return this.name(); // returns "ROLE_ADMIN" or "ROLE_USER"
    }
}
