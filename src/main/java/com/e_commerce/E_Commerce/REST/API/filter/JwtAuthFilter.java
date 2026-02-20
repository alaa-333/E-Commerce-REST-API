package com.e_commerce.E_Commerce.REST.API.filter;

import com.e_commerce.E_Commerce.REST.API.service.CustomUserDetailsService;
import com.e_commerce.E_Commerce.REST.API.util.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        // Skip if no Authorization header or not Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            
            if (jwtService.validateToken(token)) {
                Claims claims = jwtService.extractAllClaims(token);
                String username = claims.getSubject();

                // Extract roles with null safety
                Object rolesObj = claims.get("roles");
                List<SimpleGrantedAuthority> authorities;
                
                if (rolesObj instanceof List<?> rolesList && !rolesList.isEmpty()) {
                    authorities = rolesList.stream()
                            .filter(role -> role instanceof String)
                            .map(role -> new SimpleGrantedAuthority((String) role))
                            .toList();
                } else {
                    log.warn("No roles found in JWT token for user: {}", username);
                    authorities = Collections.emptyList();
                }

                UserDetails userDetails = new User(username, "", authorities);

                UsernamePasswordAuthenticationToken authenticationToken = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                        
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.debug("Authentication set for user: {}", username);
            }
        } catch (Exception e) {
            log.error("Error processing JWT token: {}", e.getMessage());
            // Continue filter chain even if token processing fails
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/v1/auth/login")
                || path.startsWith("/api/v1/auth/register")
                || path.startsWith("/api/v1/auth/refresh")
                || path.startsWith("/api/v1/auth/signup");
    }
}
