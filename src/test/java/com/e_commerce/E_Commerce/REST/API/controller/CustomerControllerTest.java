package com.e_commerce.E_Commerce.REST.API.controller;

import com.e_commerce.E_Commerce.REST.API.dto.request.CustomerUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.CustomerResponseDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.PaginationResponseDto;
import com.e_commerce.E_Commerce.REST.API.model.Customer;
import com.e_commerce.E_Commerce.REST.API.model.User;
import com.e_commerce.E_Commerce.REST.API.service.CustomerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.e_commerce.E_Commerce.REST.API.dto.request.PaginationRequestDto;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockHttpServletRequestDsl.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private CustomerService customerService;

        @Autowired
        private ObjectMapper objectMapper;

        private Customer customer1;
        private Customer customer2;
        private Customer customer3;
        private PaginationResponseDto responseDto;

        @BeforeEach
        void setUp() {
                customer1 = Customer.builder()
                                .user(any(User.class))
                                .firstName("alaa")
                                .lastName("mohamed")
                                .build();
                customer2 = Customer.builder()
                                .user(any(User.class))
                                .firstName("ahmed")
                                .lastName("sayed")
                                .build();
                customer3 = Customer.builder()
                                .user(any(User.class))
                                .firstName("mo")
                                .lastName("salah")
                                .build();

        }

        @Nested
        @DisplayName("get customer by id test class") // ok
        class GetCustomerById {

                @Test
                void getCustomer() throws Exception {
                        var customerId = 1L;
                        mockMvc.perform(get("/api/{id}", is(customerId)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.id", is(customerId)));

                        // verify
                        verify(customerService, times(1)).getCustomerById(customerId);
                }

                @Test
                void getCustomer_whenIdNotExist_shouldReturnBadRequest() throws Exception {
                        var customerId = 50L;

                        mockMvc.perform(get("/api/{id}", is(customerId)))

                                        .andExpect(status().isBadRequest());

                        // verify
                        verify(customerService, times(1)).getCustomerById(customerId);
                }
        }

        @Nested
        @DisplayName("update customer test class")
        class UpdateCustomer {

                @Test
                void updateCustomer_whenIdExist_shouldBeUpdated() throws Exception {

                        var customerId = 1L;
                        CustomerResponseDTO responseDTO = CustomerResponseDTO.builder()
                                        .id(customerId)
                                        .firstName("alaa")
                                        .build();

                        var updateRequest = CustomerUpdateRequestDTO.builder()
                                        .firstName("alaa")
                                        .build();

                        when(customerService.updateCustomer(eq(customerId), eq(updateRequest)))
                                        .thenReturn(responseDTO);

                        mockMvc.perform(put("/api/v1/customers/{id}", is(customerId))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(updateRequest)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.firstName", is("alaa")));

                        verify(customerService, times(1)).updateCustomer(eq(customerId), eq(updateRequest));
                }

                @Test
                void updateCustomer_whenInvalidId_shouldReturnBadRequest() throws Exception {

                        var customerId = -1L;

                        mockMvc.perform(put("/api/v1/customers/{id}", is(customerId)))
                                        .andExpect(status().isNotFound());

                        verify(customerService).updateCustomer(anyLong(), any(CustomerUpdateRequestDTO.class));
                }

        }

        @Nested
        @DisplayName("get all test class")
        class GetAll {
                @Test
                void getAllCustomers() throws Exception {

                        List<Customer> customers = new ArrayList<>();
                        customers.add(customer1);
                        customers.add(customer2);
                        customers.add(customer3);
                        responseDto.setData(customers);

                        when(customerService.getAllCustomers(any(PaginationRequestDto.class)))
                                        .thenReturn(responseDto);

                        mockMvc.perform(get("/api/v1/customers")).andExpect(status().isOk())
                                        .andExpect(jsonPath("$.size()", is(customers.size())))
                                        .andExpect(jsonPath("$.[0].firstName", is("alaa")));

                        verify(customerService, times(1)).getAllCustomers(any(PaginationRequestDto.class));
                }
        }

}