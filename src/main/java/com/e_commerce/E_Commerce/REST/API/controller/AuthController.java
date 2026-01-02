package com.e_commerce.E_Commerce.REST.API.controller;

import com.e_commerce.E_Commerce.REST.API.dto.request.LoginRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.request.RefreshTokenRequest;
import com.e_commerce.E_Commerce.REST.API.dto.request.SignupRequestDto;
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

    private final  JwtService jwtService;

    // =========== LOGIN =============

    @PostMapping("/login")
    public ResponseEntity<?> login( @RequestBody @Valid LoginRequestDto requestDto) {

        JwtResponse response = authService.login(requestDto);
        // Set cookies
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildAccessTokenCookie(response.getAccessToken()).toString())
                .header(HttpHeaders.SET_COOKIE, buildRefreshTokenCookie(response.getRefreshToken()).toString())
                .header(HttpHeaders.SET_COOKIE, buildCsrfCookie().toString()) // CSRF token
                .body(Map.of("message", "Login successful"));
    }

    // =========== REFRESH =============
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request)
    {

        return ResponseEntity.ok(authService.refreshToken(request));

    }

    // =========== REGISTER =============
    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register( @RequestBody @Valid SignupRequestDto requestDto)
    {
        return ResponseEntity.ok(authService.register(requestDto));
    }




    // 🛠️ Helpers
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
                .secure(true) // HTTPS only
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
                .path("/refresh") // restrict to /refresh endpoint
                .maxAge(jwtService.getRefreshExpiration() / 1000)
                .build();
    }

    private ResponseCookie buildCsrfCookie() {
        String csrfToken = UUID.randomUUID().toString();
        return ResponseCookie.from("XSRF-TOKEN", csrfToken) // readable by JS
                .httpOnly(false) // ✅ required so JS can read & send in header
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(jwtService.getJwtExpiration() / 1000)
                .build();
    }




}
