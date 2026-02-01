package com.e_commerce.E_Commerce.REST.API.controller;

import com.e_commerce.E_Commerce.REST.API.dto.request.UserUpdateRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.response.UserResponse;
import com.e_commerce.E_Commerce.REST.API.mapper.UserMapper;
import com.e_commerce.E_Commerce.REST.API.model.Customer;
import com.e_commerce.E_Commerce.REST.API.model.User;
import com.e_commerce.E_Commerce.REST.API.model.enums.Role;
import com.e_commerce.E_Commerce.REST.API.service.UserService;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;



@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserMapper mapper;

    @MockitoBean
    private UserService userService;
    @Autowired
    private UserMapper userMapper;


    // ==============  getById ==================
    @Test
    void getById_whenUserExist_shouldReturnUser() throws Exception {

        // arrange data

        String email = "333alaamo@gmail.com";
        Long userId = 1L;

        User user = User.builder()
                .email(email)
                .customer(
                        Customer.builder()
                                .firstName("alaa")
                                .build()
                )
                .build();


        when(userService.getById(userId)).thenReturn(userMapper.toResponse(user));

        // act
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value(email));


        // verify
        verify(userService).getById(userId);


    }

    void getById_whenUserDoesNotExist_shouldReturnNotFoundResponse() throws Exception {
        // arrange
        when(userService.getById(anyLong())).thenThrow(
                new UsernameNotFoundException("user not found")
        );

        // act + assett
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isNotFound());

    }


    // ==============  updateUser ==================
    @Test
    void updateUser_whenUserExist_shouldUpdated() throws Exception {


        when(userService.updateUser(anyLong(),any(UserUpdateRequestDto.class))).thenReturn(
                UserResponse.builder()
                        .id(1L)
                        .email("111lollol@gmail.com")
                        .build()
        );

        //act + asset
        mockMvc.perform(put("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("111lollol@gmail.com"));

                // verify
        verify(userService).updateUser(anyLong(),any(UserUpdateRequestDto.class));


    }


    // ==============  deleteById ==================

    @Test
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

//@WebMvcTest Explained:

//Only loads the web layer (controllers, filters, etc.)
//Auto-configures MockMvc
//Does not load services, repositories, or database
//Use @MockBean to provide mocked dependencies
//Faster than full integration tests
//Perfect for testing controller logic in isolation


//MockMvc Methods:

//perform(): Execute HTTP request
//andExpect(): Assert response properties
//andDo(): Perform additional actions (like print())
//jsonPath(): Assert JSON response content using JSONPath