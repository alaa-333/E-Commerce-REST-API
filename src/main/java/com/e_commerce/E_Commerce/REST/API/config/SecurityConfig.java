package com.e_commerce.E_Commerce.REST.API.config;

import com.e_commerce.E_Commerce.REST.API.filter.JwtAuthFilter;
import com.e_commerce.E_Commerce.REST.API.model.enums.Role;
import com.e_commerce.E_Commerce.REST.API.repository.UserRepository;
import com.e_commerce.E_Commerce.REST.API.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
i
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;



    // enable basic authentication
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                 .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        // 1. PUBLIC: Authentication & Registration (Open to everyone)
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // 2. PUBLIC (READ-ONLY): Guests should be able to view products
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()

                        // 3. ADMIN ONLY: Product Management & Customer Admin
                        // Using hasAuthority to match "ROLE_ADMIN" exactly
                        .requestMatchers(HttpMethod.POST, "/api/v1/products/**").hasAuthority(Role.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/api/v1/products/**").hasAuthority(Role.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**").hasAuthority(Role.ROLE_ADMIN.name())
                        .requestMatchers("/api/v1/customers/**").hasAuthority(Role.ROLE_ADMIN.name())

                        // 4. AUTHENTICATED USERS (Customer + Admin): Orders & Payments
                        // ideally, specific order access should be protected by logic (userid check)
                        .requestMatchers("/api/v1/orders/**").hasAnyAuthority(Role.ROLE_USER.name(), Role.ROLE_ADMIN.name())
                        .requestMatchers("/api/v1/payments/**").hasAnyAuthority(Role.ROLE_USER.name(), Role.ROLE_ADMIN.name())

                        // 5. Catch-all for anything else
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository)
    {
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }







}
