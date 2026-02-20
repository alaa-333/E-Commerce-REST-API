package com.e_commerce.E_Commerce.REST.API.controller;

import com.e_commerce.E_Commerce.REST.API.dto.request.UserUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.UserResponse;
import com.e_commerce.E_Commerce.REST.API.mapper.UserMapper;
import com.e_commerce.E_Commerce.REST.API.model.Customer;
import com.e_commerce.E_Commerce.REST.API.model.User;
import com.e_commerce.E_Commerce.REST.API.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("User Controller Test")
class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private UserService userService;

        @MockitoBean
        private com.e_commerce.E_Commerce.REST.API.util.JwtService jwtService;

        @MockitoBean
        private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

        @Autowired
        private UserMapper userMapper;

        @Nested
        @DisplayName("Get User By ID Tests")
        class GetById {
                @Test
                @DisplayName("Should return user when user exists")
                void getById_whenUserExist_shouldReturnUser() throws Exception {

                        // arrange data
                        String email = "333alaamo@gmail.com";
                        Long userId = 1L;

                        User user = User.builder()
                                        .email(email)
                                        .customer(
                                                        Customer.builder()
                                                                        .firstName("alaa")
                                                                        .build())
                                        .build();

                        when(userService.getById(userId)).thenReturn(userMapper.toResponse(user));

                        // act
                        mockMvc.perform(get("/api/v1/users/1"))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$.id").value(userId))
                                        .andExpect(jsonPath("$.email").value(email));

                        // verify
                        verify(userService).getById(userId);
                }

                @Test
                @DisplayName("Should return NotFound when user does not exist")
                void getById_whenUserDoesNotExist_shouldReturnNotFoundResponse() throws Exception {
                        // arrange
                        when(userService.getById(anyLong())).thenThrow(
                                        new UsernameNotFoundException("user not found"));

                        // act + assert
                        mockMvc.perform(get("/api/v1/users/1"))
                                        .andExpect(status().isNotFound());
                }
        }

        @Nested
        @DisplayName("Update User Tests")
        class UpdateUser {
                @Test
                @DisplayName("Should update user when user exists")
                void updateUser_whenUserExist_shouldUpdated() throws Exception {

                        when(userService.updateUser(anyLong(), any(UserUpdateRequestDTO.class))).thenReturn(
                                        UserResponse.builder()
                                                        .id(1L)
                                                        .email("111lollol@gmail.com")
                                                        .build());

                        // act + assert
                        mockMvc.perform(put("/api/v1/users/1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{}")) // Added empty body to satisfying request requirements if any
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.id").value(1L))
                                        .andExpect(jsonPath("$.email").value("111lollol@gmail.com"));

                        // verify
                        verify(userService).updateUser(anyLong(), any(UserUpdateRequestDTO.class));
                }
        }

        @Nested
        @DisplayName("Delete User By ID Tests")
        class DeleteById {
                @Test
                @DisplayName("Should delete user when user exists")
                void deleteById_whenUserExist_shouldDeleted() throws Exception {
                        // arrange
                        doNothing().when(userService).deleteUserById(anyLong());

                        // act
                        mockMvc.perform(delete("/api/v1/users/1"))
                                        .andExpect(status().isNoContent());

                        // verify
                        verify(userService).deleteUserById(anyLong());
                }

                @Test
                @DisplayName("Should return NotFound when user does not exist")
                void deleteById_whenUserDoesNotExist_shouldReturnNotFoundResponse() throws Exception {
                        // arrange
                        doThrow(new UsernameNotFoundException("user not found"))
                                        .when(userService)
                                        .deleteUserById(anyLong());

                        // act + asserting
                        mockMvc.perform(delete("/api/v1/users/1"))
                                        .andExpect(status().isNotFound());

                        verify(userService).deleteUserById(anyLong());
                }
        }
}
