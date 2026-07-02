package com.ecommerce.api.unit.service;

import com.ecommerce.api.dto.request.auth.LoginRequest;
import com.ecommerce.api.dto.request.auth.RefreshTokenRequest;
import com.ecommerce.api.dto.request.auth.RegisterRequest;
import com.ecommerce.api.dto.response.AuthResponse;
import com.ecommerce.api.entity.RefreshToken;
import com.ecommerce.api.entity.User;
import com.ecommerce.api.entity.enums.Role;
import com.ecommerce.api.exception.DuplicateResourceException;
import com.ecommerce.api.exception.EcommerceAppException;
import com.ecommerce.api.repository.RefreshTokenRepository;
import com.ecommerce.api.repository.UserRepository;
import com.ecommerce.api.security.JwtService;
import com.ecommerce.api.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("AuthService Unit Tests")
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private AuthService authService;

    // ============== Arrange data ===========//
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private RefreshTokenRequest refreshTokenRequest;
    private RefreshToken refreshToken;
    private User user;
    private List<RefreshToken> refreshTokens;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .email("alaa@gmail.com")
                .firstName("Alaa")
                .lastName("Mohamed")
                .password("123456")
                .build();

        user = User.builder()
                .username(registerRequest.getEmail())
                .password("plain_password")
                .enabled(true)
                .accountNonLocked(true)
                .roles(Set.of(Role.ROLE_USER))
                .build();
        user.setId(1L);


        loginRequest = LoginRequest.builder()
                .email("alaa@gmail.com")
                .password("123456")
                .build();

        refreshTokenRequest = RefreshTokenRequest.builder()
                .refreshToken("sample_refresh_token")
                .build();

        refreshToken = RefreshToken.builder()
                .userId(1L)
                .tokenHash("sample_refresh_token_hash")
                .userRoles(Set.of("ROLE_USER"))
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();

        refreshTokens = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            refreshTokens.add(
                    RefreshToken.builder()
                            .userId(user.getId())
                            .userRoles(Set.of("ROLE_USER"))
                            .revoked(true)
                            .tokenHash("token_hash_" + i)
                            .revokedAt(LocalDateTime.now())
                            .expiresAt(LocalDateTime.now().minusHours(1))
                            .build()
            );
        }

        refreshTokens.add(refreshToken);
    }

    @DisplayName("Register Test")
    @Nested
    class RegisterTest {
        @Test
        @DisplayName("Given valid register request when registering user then return user response")
        public void registerRequest_whenValidRequest_shouldReturnUserResponse() {
            // arrange
            when(userRepository.existsByUsername(registerRequest.getEmail())).thenReturn(false);
            when(encoder.encode(registerRequest.getPassword())).thenReturn("encoded_password");
            when(userRepository.save(any(User.class))).thenReturn(user);

            // act
            var result = authService.register(registerRequest);

            // assert
            assertThat(result).isNotNull();
            assertThat(result.email()).isEqualTo(registerRequest.getEmail());
            assertThat(result.roles()).contains(Role.ROLE_USER);

            // verify
            verify(userRepository).existsByUsername(registerRequest.getEmail());
            verify(encoder).encode(registerRequest.getPassword());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Given existing username when registering then throw DuplicateResourceException")
        public void registerRequest_whenInvalidRequest_shouldThrowException() {
            //arrange
            when(userRepository.existsByUsername(registerRequest.getEmail())).thenReturn(true);

            // act + assert
            assertThrows(DuplicateResourceException.class, () -> authService.register(registerRequest));

            // verify
            verify(userRepository).existsByUsername(registerRequest.getEmail());
            verify(encoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @DisplayName("Login Test")
    @Nested
    class LoginTest {

        @Test
        @DisplayName("Given valid credentials when login then return tokens")
        public void loginRequest_whenValidRequest_shouldReturnAuthResponse() {
            // arrange: AuthenticationManager is used by AuthService.login — stub it to return Authentication with principal = user
            Authentication auth = mock(Authentication.class);
            when(auth.getPrincipal()).thenReturn(user);
            when(authenticationManager.authenticate(any())).thenReturn(auth);

            when(jwtService.generateAccessToken(user)).thenReturn("test_jwt_token");
            when(jwtService.generateRefreshToken(user)).thenReturn("sample_refresh_token");
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

            // act
            AuthResponse result = authService.login(loginRequest);

            // assert
            assertThat(result.accessToken()).isEqualTo("test_jwt_token");
            assertThat(result.refreshToken()).isEqualTo("sample_refresh_token");

            // verify
            verify(authenticationManager).authenticate(any());
            verify(jwtService).generateAccessToken(user);
            verify(jwtService).generateRefreshToken(user);
            verify(refreshTokenRepository).save(any(RefreshToken.class));
        }

        @Test
        @DisplayName("Given invalid credentials when login then throw AuthenticationException")
        public void loginRequest_whenInvalidRequest_shouldThrowAuthenticationException() {
            // arrange
            when(authenticationManager.authenticate(any())).thenThrow(mock(AuthenticationException.class));

            // act + assert
            assertThrows(AuthenticationException.class, () -> authService.login(loginRequest));

            // verify
            verify(authenticationManager).authenticate(any());
            verifyNoInteractions(jwtService);
            verifyNoInteractions(refreshTokenRepository);
        }
    }

    @DisplayName("get Refresh Token test")
    @Nested
    class RefreshTokenTest {
        @Test
        @DisplayName("Given valid refresh token when requesting new tokens then return new tokens")
        public void getRefreshToken_whenValidTokenRequest_shouldReturnAuthResponse() {
            when(jwtService.extractUsername(refreshTokenRequest.getRefreshToken())).thenReturn(user.getUsername());
            when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
            when(refreshTokenRepository.findByUserId(user.getId())).thenReturn(refreshTokens);
            when(encoder.matches(eq(refreshTokenRequest.getRefreshToken()), anyString())).thenAnswer(i -> {
                String storedHash = i.getArgument(1);
                return storedHash.equals(refreshToken.getTokenHash());
            });

            when(jwtService.generateAccessToken(user)).thenReturn("new_access_token");
            when(jwtService.generateRefreshToken(user)).thenReturn("new_refresh_token");

            // act
            var result = authService.getRefreshToken(refreshTokenRequest);

            // assert
            assertThat(result).isNotNull();
            assertThat(result.refreshToken()).isEqualTo("new_refresh_token");
            assertThat(result.accessToken()).isEqualTo("new_access_token");

            // verify
            verify(jwtService).extractUsername(any(String.class));
            verify(jwtService).generateAccessToken(any(User.class));
            verify(jwtService).generateRefreshToken(any(User.class));
            verify(userRepository).findByUsername(any(String.class));
            verify(refreshTokenRepository, atLeastOnce()).save(any(RefreshToken.class));
        }

        @Test
        @DisplayName("Given invalid refresh token when requesting new tokens then throw EcommerceAppException")
        public void getRefreshToken_whenInvalidTokenRequest_shouldThrowException() {
            when(jwtService.extractUsername(refreshTokenRequest.getRefreshToken())).thenReturn(user.getUsername());
            when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
            when(refreshTokenRepository.findByUserId(user.getId())).thenReturn(refreshTokens);
            when(encoder.matches(eq(refreshTokenRequest.getRefreshToken()), anyString())).thenReturn(false);

            assertThrows(EcommerceAppException.class, () -> authService.getRefreshToken(refreshTokenRequest));

            verify(jwtService).extractUsername(any(String.class));
            verify(userRepository).findByUsername(any(String.class));
        }
    }

}