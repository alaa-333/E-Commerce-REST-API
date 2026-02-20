package com.e_commerce.E_Commerce.REST.API.controller;

import com.e_commerce.E_Commerce.REST.API.dto.request.LoginRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.RefreshTokenRequest;
import com.e_commerce.E_Commerce.REST.API.dto.request.SignupRequestDTO;
import com.e_commerce.E_Commerce.REST.API.service.AuthService;
import com.e_commerce.E_Commerce.REST.API.util.JwtService;
import com.e_commerce.E_Commerce.REST.API.util.JwtResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO requestDto) {

        JwtResponse response = authService.login(requestDto);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildAccessTokenCookie(response.getAccessToken()).toString())
                .header(HttpHeaders.SET_COOKIE, buildRefreshTokenCookie(response.getRefreshToken()).toString())
                .header(HttpHeaders.SET_COOKIE, buildCsrfCookie().toString())
                .body(Map.of("message", "Login successful"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {

        return ResponseEntity.ok(authService.refreshToken(request));

    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@RequestBody @Valid SignupRequestDTO requestDto) {
        return ResponseEntity.ok(authService.register(requestDto));
    }

    private String getCookieValue(HttpServletRequest req, String name) {
        return Optional.ofNullable(req.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(c -> name.equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    private ResponseCookie buildAccessTokenCookie(String token) {
        return ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("lax")
                .path("/auth/login")
                .maxAge(jwtService.getJwtExpiration() / 1000)
                .build();
    }

    private ResponseCookie buildRefreshTokenCookie(String token) {
        return ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/refresh")
                .maxAge(jwtService.getRefreshExpiration() / 1000)
                .build();
    }

    private ResponseCookie buildCsrfCookie() {
        String csrfToken = UUID.randomUUID().toString();
        return ResponseCookie.from("XSRF-TOKEN", csrfToken)
                .httpOnly(false)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(jwtService.getJwtExpiration() / 1000)
                .build();
    }

}
