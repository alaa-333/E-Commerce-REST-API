package com.e_commerce.E_Commerce.REST.API.util;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class JwtResponse {

    @Builder.Default
    private String type = "Bearer";

    private String subject;
    private String accessToken;
    private String refreshToken;
    private String expiration;

    private Set<String> roles;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();


}
