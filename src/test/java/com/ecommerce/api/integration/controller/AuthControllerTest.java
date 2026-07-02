package com.ecommerce.api.integration.controller;

import com.ecommerce.api.controller.AuthController;
import com.ecommerce.api.dto.request.auth.LoginRequest;
import com.ecommerce.api.dto.request.auth.RefreshTokenRequest;
import com.ecommerce.api.dto.request.auth.RegisterRequest;
import com.ecommerce.api.dto.response.AuthResponse;
import com.ecommerce.api.dto.response.UserResponse;
import com.ecommerce.api.entity.enums.Role;
import com.ecommerce.api.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("AuthController Unit Tests (WebMvc Slice)")
@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private final String ACCESS_TOKEN = "15TDGHv+K9L0rT5sD7X3zA4bU6mN8cW1eF0G2hI7j_access_token";
    private final String REFRESH_TOKEN = "9L0rT5sD7X3zA4bU6mN8cW1eF0G2hI7jL954DGHS_refresh_token";

    @DisplayName("Register Endpoint Tests")
    @Nested
    class RegisterTests {
        private RegisterRequest registerRequest;
        private UserResponse userResponse;

        @BeforeEach
        void setUp() {
            registerRequest = RegisterRequest.builder()
                    .email("alaa@gmail.com")
                    .firstName("Alaa")
                    .lastName("Mohamed")
                    .password("123456")
                    .build();

            userResponse = new UserResponse(
                    1L, "alaa@gmail.com", Set.of(Role.ROLE_USER), LocalDateTime.now()
            );
        }

        @DisplayName("POST /auth/register - Should return 200 and UserResponse when registration is successful")
        @Test
        public void registerNewUser_whenValidRequest_shouldReturn200WithUserResponse() throws Exception {
            // arrange
            when(authService.register(registerRequest)).thenReturn(userResponse);

            // act & assert
            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerRequest))
            )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Registration successful"))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.email").value("alaa@gmail.com"))
                    .andExpect(jsonPath("$.data.roles[0]").value("ROLE_USER"));
        }
    }

    @DisplayName("Login Endpoint Tests")
    @Nested
    class LoginTests {
        private LoginRequest loginRequest;
        private AuthResponse authResponse;

        @BeforeEach
        void setUp() {
            loginRequest = LoginRequest.builder()
                    .email("lol@gmail.com")
                    .password("12345678")
                    .build();

            authResponse = new AuthResponse(
                    ACCESS_TOKEN,
                    REFRESH_TOKEN,
                    "bearer",
                    7L
            );
        }

        @DisplayName("POST /auth/login - Should return 200 and AuthResponse when login is successful")
        @Test
        public void loginUser_whenValidRequest_shouldReturn200WithAuthResponse() throws Exception {
            // arrange
            when(authService.login(loginRequest)).thenReturn(authResponse);

            // act & assert
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest))
            )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Login successful"))
                    .andExpect(jsonPath("$.data.accessToken").value(ACCESS_TOKEN))
                    .andExpect(jsonPath("$.data.refreshToken").value(REFRESH_TOKEN))
                    .andExpect(jsonPath("$.data.tokenType").value("bearer"));
        }
    }

    @DisplayName("Refresh Token Endpoint Tests")
    @Nested
    class RefreshTokenTests {
        private RefreshTokenRequest refreshTokenRequest;
        private AuthResponse authResponse;

        @BeforeEach
        void setUp() {
            refreshTokenRequest = RefreshTokenRequest.builder()
                    .refreshToken(REFRESH_TOKEN)
                    .build();

            authResponse = new AuthResponse(
                    ACCESS_TOKEN,
                    REFRESH_TOKEN,
                    "bearer",
                    7L
            );
        }

        @DisplayName("POST /auth/refresh - Should return 200 and new AuthResponse when refresh token is valid")
        @Test
        public void refreshToken_whenValidToken_shouldReturn200WithNewAuthResponse() throws Exception {
            // arrange
            when(authService.getRefreshToken(refreshTokenRequest)).thenReturn(authResponse);

            // act & assert
            mockMvc.perform(post("/auth/refresh")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(refreshTokenRequest))
            )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("user login successfully"))
                    .andExpect(jsonPath("$.data.accessToken").value(ACCESS_TOKEN))
                    .andExpect(jsonPath("$.data.refreshToken").value(REFRESH_TOKEN))
                    .andExpect(jsonPath("$.data.tokenType").value("bearer"));
        }
    }
}
