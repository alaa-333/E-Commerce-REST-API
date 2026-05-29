package com.ecommerce.api.service;

import com.ecommerce.api.dto.request.auth.LoginRequest;
import com.ecommerce.api.dto.request.auth.RefreshTokenRequest;
import com.ecommerce.api.dto.request.auth.RegisterRequest;
import com.ecommerce.api.dto.response.AuthResponse;
import com.ecommerce.api.dto.response.UserResponse;
import com.ecommerce.api.entity.RefreshToken;
import com.ecommerce.api.entity.User;
import com.ecommerce.api.entity.enums.Role;
import com.ecommerce.api.exception.DuplicateResourceException;
import com.ecommerce.api.exception.EcommerceAppException;
import com.ecommerce.api.exception.ErrorCode;
import com.ecommerce.api.exception.ResourceNotFoundException;
import com.ecommerce.api.repository.RefreshTokenRepository;
import com.ecommerce.api.repository.UserRepository;
import com.ecommerce.api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder encoder;


    public UserResponse register(RegisterRequest request) {

        // check if user is existed first
        if (userRepository.existsByUsername(request.getEmail())) {
            throw new DuplicateResourceException(ErrorCode.USER_ALREADY_EXIST, ErrorCode.USER_ALREADY_EXIST.getMessage());
        }

        // create user object
        var user = User.builder().username(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .enabled(true)
                .accountNonLocked(true)
                .roles(Set.of(Role.ROLE_USER))
                .build();

        // TODO: create customer obj && save into db

        // save user into db
        var savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getRoles(),
                LocalDateTime.now()

        );
    }

    public AuthResponse login(LoginRequest request) {

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        var userDetails =(User) auth.getPrincipal();
        var accessToken = jwtService.generateAccessToken(userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);
        var refreshTokenEntity = RefreshToken.builder()
                .userId(userDetails.getId())
                .tokenHash(encoder.encode(refreshToken))
                .userRoles(userDetails.getAuthorities().stream().map(authObj -> authObj.getAuthority()).collect(Collectors.toSet()))
                .expiresAt(LocalDateTime.now().plusSeconds(jwtService.getRefreshExpiration() / 1000))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getExpiration()
        );

    }

    public AuthResponse getRefreshToken(RefreshTokenRequest request) {

        // Extract username from the refresh token
        String username = jwtService.extractUsername(request.getRefreshToken());

        // Load user from database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND,
                        "User not found with username: " + username
                ));

        // Find all tokens for this user
        List<RefreshToken> tokens = refreshTokenRepository.findByUserId(user.getId());

        // Find the matching token using matches() instead of equality
        var oldToken = tokens.stream()
                .filter(rt -> encoder.matches(request.getRefreshToken(), rt.getTokenHash()))
                .filter(rt -> !rt.isRevoked())
                .filter(rt -> rt.getExpiresAt().isAfter(LocalDateTime.now()))
                .findFirst()
                .orElseThrow(() -> new EcommerceAppException(
                        ErrorCode.INVALID_OR_MALFORMED_REFRESH_TOKEN,
                        "Invalid refresh token"
                ));

        oldToken.setRevoked(true);
        oldToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(oldToken);

        var newRefreshToken = jwtService.generateRefreshToken(user);
        var newAccessToken = jwtService.generateAccessToken(user);

        var refreshTokenEntity = RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(encoder.encode(newRefreshToken))
                .userRoles(user.getRoles().stream().map(Enum::toString).collect(Collectors.toSet()))
                .expiresAt(LocalDateTime.now().plusSeconds(jwtService.getRefreshExpiration() / 1000))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                jwtService.getExpiration()
        );
    }
}
