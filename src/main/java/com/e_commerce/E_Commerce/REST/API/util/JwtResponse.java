package com.e_commerce.E_Commerce.REST.API.util;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class JwtResponse {

    @Builder.Default
    private String type = "Bearer";

    private String accessToken;
    private String refreshToken;
    private Long expiration;
    private String username;

    private Collection<? extends GrantedAuthority> roles;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();


}
