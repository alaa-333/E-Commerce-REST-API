package com.e_commerce.E_Commerce.REST.API.filter;

import com.e_commerce.E_Commerce.REST.API.service.CustomUserDetailsService;
import com.e_commerce.E_Commerce.REST.API.util.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
@Component
public class JwtAuthFilter  extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
      String authHeader = request.getHeader("Authorization");
      String token = null;
      String username = null;


        // Safer: skip if no Authorization header or path is public
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
      token = authHeader.substring(7);
      if (jwtService.validateToken(token))
      {
          Claims claims = jwtService.extractAllClaims(token);
          username = claims.getSubject();

          // ====== extract roles ====
          List<SimpleGrantedAuthority> authorities = ((List<?>) claims.get("roles")).stream()
                  .map(authority -> new SimpleGrantedAuthority((String) authority))
                  .toList();
          UserDetails userDetails = new User(username , "", authorities);

          UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                  userDetails,
                  null,
                  authorities

          );
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);

      }

      filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/v1/auth/login")
                || path.startsWith("/api/v1/auth/register")
                || path.startsWith("/api/v1/auth/refresh");
    }
}
