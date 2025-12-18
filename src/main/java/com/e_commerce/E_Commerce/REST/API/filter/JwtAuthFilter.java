package com.e_commerce.E_Commerce.REST.API.filter;

import com.e_commerce.E_Commerce.REST.API.service.CustomUserDetailsService;
import com.e_commerce.E_Commerce.REST.API.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Component
public class JwtAuthFilter  extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
      String authHeader = request.getHeader("Authorization");
      String token = null;
      String username = null;


      if (authHeader != null && authHeader.startsWith("Bearer ")) {
          token = authHeader.substring(7);
      }
      if (jwtTokenUtil.validateToken(token))
      {
          Claims claims = jwtTokenUtil.extractAllClaims(token);
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

          // delegate to next filter
          filterChain.doFilter(request, response);
      }

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Skip filter for certain endpoints
        return request.getServletPath().equals("/auth/login")
                || request.getServletPath().equals("/auth/register")
                || request.getServletPath().equals("/auth/refresh");
    }
}
