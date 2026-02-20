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
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    private final JwtAuthFilter authFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/products/**").hasAuthority(Role.ROLE_ADMIN.toString())
                        .requestMatchers(HttpMethod.PUT, "/api/v1/products/**").hasAuthority(Role.ROLE_ADMIN.toString())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**").hasAuthority(Role.ROLE_ADMIN.toString())
                        .requestMatchers("/api/v1/customers/**").hasAuthority(Role.ROLE_ADMIN.toString())
                        .requestMatchers("/api/v1/orders/**").hasAnyAuthority(Role.ROLE_USER.toString(), Role.ROLE_ADMIN.toString())
                        .requestMatchers("/api/v1/payments/**").hasAnyAuthority(Role.ROLE_USER.toString(), Role.ROLE_ADMIN.toString())
                        .anyRequest().authenticated()
                )
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new CustomUserDetailsService(userRepository);
    }

    // ✅ Fixed: Correct provider setup
    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}