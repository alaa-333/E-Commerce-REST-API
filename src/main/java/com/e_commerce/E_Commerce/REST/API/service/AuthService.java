package com.e_commerce.E_Commerce.REST.API.service;

import com.e_commerce.E_Commerce.REST.API.dto.request.LoginRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.request.RefreshTokenRequest;
import com.e_commerce.E_Commerce.REST.API.dto.request.SignupRequestDto;
import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;
import com.e_commerce.E_Commerce.REST.API.model.User;
import com.e_commerce.E_Commerce.REST.API.model.enums.Role;
import com.e_commerce.E_Commerce.REST.API.util.JwtResponse;
import com.e_commerce.E_Commerce.REST.API.util.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.server.Cookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;

    public JwtResponse login (LoginRequestDto requestDto)
    {
        // 2. Authenticate
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 4. Generate Token
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.refreshToken(userDetails.getUsername());
        String sub = jwtService.extractClaim(token , Claims::getSubject);
        Date expiration = jwtService.extractClaim(token , Claims::getExpiration);
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.toSet());


        // 5. Build Response
        return  JwtResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .subject(sub)
                .expiration(expiration.toString())
                .roles(roles)
                .build();




    }

    public JwtResponse register(SignupRequestDto requestDto)
    {

        if (userService.existByEmail(requestDto.getUserCreateRequestDto().getEmail()))
        {
            throw new ValidationException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User user = userService.createUser(requestDto);
        String token = jwtService.generateToken(user);
        String sub = jwtService.extractClaim(token , Claims::getSubject);
        String refreshToken = jwtService.refreshToken(user.getUsername());
        String expiration = jwtService.extractClaim(token, Claims::getExpiration).toString();
        Set<String> roles = user.getRoles()
                .stream()
                .map(Role::getAuthority)
                .collect(Collectors.toSet());


        return  JwtResponse.builder()
                        .accessToken(token)
                        .refreshToken(refreshToken)
                        .expiration(expiration)
                        .subject(sub)
                        .roles(roles)
                        .build();
    }

    public JwtResponse refreshToken(RefreshTokenRequest request)
    {
        String refreshToken = request.getRefreshToken();

        if (!jwtService.validateToken(refreshToken))
        {
            throw new ValidationException(ErrorCode.TOKEN_INVALID);
        }

        String username = jwtService.extractUsername(refreshToken);
        UserDetails user = userDetailsService.loadUserByUsername(username);
        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.refreshToken(username);

        String expiration = jwtService.extractClaim(newAccessToken,Claims::getExpiration).toString();
        Set<String> roles = user.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.toSet());

        // build response
        return JwtResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .subject(username)
                .expiration(expiration)
                .roles(roles)
                .build();


    }
}
