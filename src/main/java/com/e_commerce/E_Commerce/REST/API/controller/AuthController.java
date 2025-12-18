package com.e_commerce.E_Commerce.REST.API.controller;

import com.e_commerce.E_Commerce.REST.API.dto.request.LoginRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.request.SignupRequestDto;
import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;
import com.e_commerce.E_Commerce.REST.API.model.User;
import com.e_commerce.E_Commerce.REST.API.service.CustomUserDetailsService;
import com.e_commerce.E_Commerce.REST.API.service.UserService;
import com.e_commerce.E_Commerce.REST.API.util.JwtTokenUtil;
import com.e_commerce.E_Commerce.REST.API.util.JwtResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;

    // =========== LOGIN =============

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login (@Valid @RequestBody LoginRequestDto requestDto)
    {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword() )
                );


        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenUtil.generateToken(userDetails);

        JwtResponse response =  JwtResponse.builder()
                .accessToken(token)
                .expiration(jwtTokenUtil.getJwtExpiration())
                .roles(userDetails.getAuthorities())
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid SignupRequestDto requestDto)
    {
        if (userService.existByEmail(requestDto.getUserCreateRequestDto().getEmail()))
        {
            throw new ValidationException(ErrorCode.DUPLICATE_ENTRY);
        }

        User user = userService.createUser(requestDto);
        String token = jwtTokenUtil.generateToken(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                 JwtResponse.builder()
                         .accessToken(token)
                         .expiration(jwtTokenUtil.getJwtExpiration())
                         .username(requestDto.getCustomerCreateRequestDto().getFirstName())
                         .roles(user.getRoles())
                         .build()
        );

    }





}
