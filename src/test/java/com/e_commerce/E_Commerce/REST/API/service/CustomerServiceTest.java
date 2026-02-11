package com.e_commerce.E_Commerce.REST.API.service;

import com.e_commerce.E_Commerce.REST.API.dto.request.CustomerUpdateReqDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.PaginationRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.response.AddressResponseDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.CustomerResponseDTO;
import com.e_commerce.E_Commerce.REST.API.exception.customer.CustomerNotFoundException;
import com.e_commerce.E_Commerce.REST.API.mapper.CustomerMapper;
import com.e_commerce.E_Commerce.REST.API.model.Address;
import com.e_commerce.E_Commerce.REST.API.model.Customer;
import com.e_commerce.E_Commerce.REST.API.model.User;
import com.e_commerce.E_Commerce.REST.API.model.enums.Role;
import com.e_commerce.E_Commerce.REST.API.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;
import static org.awaitility.Awaitility.given;
import static org.junit.jupiter.api.Assertions.*;
import static  com.e_commerce.E_Commerce.REST.API.dto.request.PaginationRequestDto.validate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("customer service class uint test")
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {


    // =============================
    // My Mocks
    // =============================
    @Mock
    private  CustomerRepository repository;
    @Mock
    private  CustomerMapper mapper;
    @InjectMocks
    private CustomerService service;


    private Customer customer;
    private Address address;
    private User user;
    private CustomerUpdateReqDTO updateReqDTO;
    private CustomerResponseDTO responseDTO;
    private PaginationRequestDto paginationDto;

    @BeforeEach
    void setUp()
    {
        address =  Address.builder()
                .city("cairo")
                .country("egypt")
                .build();

        user = User.builder()
                .email("user@gmail.com")
                .customer(customer)
                .id(1L)
                .password("123qwe!@#")
                .roles(Set.of(Role.ROLE_ADMIN))
                .build();

        customer =  Customer.builder()

                .id(1L)
                .firstName("alaa")
                .lastName("mohamed")
                .address(address)
                .phone("01062315586")
                .user(user)
                .build();

        responseDTO = CustomerResponseDTO.builder()
                .address(new AddressResponseDTO(
                        address.getCity(),
                        address.getStreet(),
                        address.getPostalCode(),
                        address.getCountry()
                ))
                .email(user.getEmail())
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email("user@gmail.com")
                .build();

        updateReqDTO =  CustomerUpdateReqDTO.builder()
                .firstName("new name")
                .email("new@gmail.com")
                .build();

         paginationDto = new PaginationRequestDto(1, 10, "id", "ASC", null);
    }


    @DisplayName("GetAll test Class")
    @Nested
    class GetAllTest{

        @Test
        void getAllCustomers() {
            // arrange

            Page<Customer> customerPage = new PageImpl<>(List.of(customer));

            when(repository.findAll(any(Pageable.class))).thenReturn(customerPage);
            when(mapper.toResponseDTO(any(Customer.class))).thenReturn(responseDTO);

            // act
            var result = service.getAllCustomers(paginationDto);

            // assert
            assertThat(result).isNotNull();
            assertThat(result.getData()).hasSize(1);
            assertThat(result.getData().get(0).id()).isEqualTo(customer.getId());

            // verify
            verify(repository).findAll(any(Pageable.class));
            verify(mapper).toResponseDTO(any(Customer.class));
        }
    }


    @DisplayName("GetCustomerById Test class")
    @Nested
    class GetCustomerByIdTest{

        @Test
        void getCustomerById_whenIdExist_shouldReturnCustomer() {

            // arrange
            var userId = 1L;
            when(repository.findById(userId))
                    .thenReturn(Optional.of(customer));
            when(mapper.toResponseDTO(customer))
                    .thenReturn(responseDTO);

            // act
            CustomerResponseDTO result = service.getCustomerById(userId);

            // assert
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(userId);
            assertThat(result.firstName()).isEqualTo(customer.getFirstName());

            // verify

            verify(repository).findById(userId);
            verify(mapper).toResponseDTO(any(Customer.class));
        }

        @Test
        void getCustomerById_whenCustomerDoesNotExist_shouldReturnException()
        {
            var unExistId = 22L;
            when(repository.findById(unExistId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy( () -> service.getCustomerById(unExistId))
                    .isExactlyInstanceOf(CustomerNotFoundException.class)
                    .hasMessage("Customer not found with identifier: "+unExistId);

            // verify
            verify(repository).findById(unExistId);
            verifyNoInteractions(mapper);
        }

    }




    @DisplayName("Update Customer Test class")
    @Nested
    class UpdateCustomerTest{

        @Test
        void updateCustomer_whenCustomerExist_shouldBeUpdated() {

            var userId = 1L;

            CustomerResponseDTO expectedUser = CustomerResponseDTO.builder()
                    .id(userId)
                    .firstName("new name")
                    .email("new@gmail.com")
                    .build();

            when(repository.findById(userId))
                    .thenReturn(Optional.of(customer));

            doNothing().when(mapper).updateEntityFromDTO(updateReqDTO, customer);
            when(mapper.toResponseDTO(customer))
                    .thenReturn(expectedUser);
            when(repository.save(any(Customer.class)))
                    .thenReturn(customer);

            // act
            CustomerResponseDTO result = service.updateCustomer(userId, updateReqDTO);

            // assert
            assertThat(result).isNotNull();
            assertThat(result.firstName()).isEqualTo("new name");
            assertThat(result.email()).isEqualTo("new@gmail.com");

            //verify
            verify(repository).findById(userId);
            verify(repository).save(any(Customer.class));
            verify(mapper).toResponseDTO(any(Customer.class));
            verify(mapper).updateEntityFromDTO(updateReqDTO, customer);

        }

        @Test
        void updateCustomer_whenCustomerDoesNotExist_shouldThrowException() {

            var userId = 1L;


            when(repository.findById(userId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy( () -> service.updateCustomer(userId, updateReqDTO))
                    .isInstanceOf(CustomerNotFoundException.class)
                    .hasMessage("Customer not found with identifier: "+userId);

            //verify
            verify(repository).findById(userId);
            verify(repository, never()).save(any(Customer.class));
            verifyNoInteractions(mapper);

        }

        @Test
        void updateCustomer_whenPartialUpdates_shouldbeUpdated() {


            var userId = 1L;

            updateReqDTO.setEmail("new@gmail.com");
            CustomerResponseDTO expectedUser = CustomerResponseDTO.builder()
                    .id(userId)
                    .firstName(null)
                    .email("new@gmail.com")
                    .build();

            when(repository.findById(userId))
                    .thenReturn(Optional.of(customer));

            doNothing().when(mapper).updateEntityFromDTO(updateReqDTO, customer);
            when(mapper.toResponseDTO(customer))
                    .thenReturn(expectedUser);
            when(repository.save(any(Customer.class)))
                    .thenReturn(customer);

            // act
            CustomerResponseDTO result = service.updateCustomer(userId, updateReqDTO);

            // assert
            assertThat(result).isNotNull();
            assertThat(result.firstName()).isEqualTo("alaa");
            assertThat(result.email()).isEqualTo("new@gmail.com");

        }

    }


}