package com.e_commerce.E_Commerce.REST.API.repository;

import com.e_commerce.E_Commerce.REST.API.dto.response.PaginationResponseDto;
import com.e_commerce.E_Commerce.REST.API.model.Customer;
import com.e_commerce.E_Commerce.REST.API.model.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@RequiredArgsConstructor
@DataJpaTest
class CustomerRepositoryTest {

    private final TestEntityManager entityManager;
    private final CustomerRepository customerRepository;


    private Customer customer1;
    private Customer customer2;
    private Customer customer3;



    @BeforeEach
    void setUp()
    {

        customerRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

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
    @DisplayName("test class to save method")
    class Save{

        @Test
        void save_whenEntityExists_shouldBeCreated()
        {

            Customer savedCustomer = customerRepository.save(customer1);

            assertNotNull(savedCustomer);
            assertEquals("alaa", savedCustomer.getFirstName());

        }
    }

    @Nested
    @DisplayName("test class to FindById method")
    class FindById{

        @Test
        void findById_whenCustomerExist_shouldReturnCustomer()
        {

            Customer savedCustomer = customerRepository.save(customer1);
            Long id = savedCustomer.getId();

            Optional<Customer> customer = customerRepository.findById(id);

            assertThat(customer)
                    .isPresent()
                    .get()
                    .extracting(Customer::getId, Customer::getFirstName)
                    .containsExactly(id, "alaa");

        }

        @Test
        void findById_whenCustomerDoesNotExist_shouldReturnOptionalOfEmpty()
        {


            Optional<Customer> customer = customerRepository.findById(22L);
            assertThat(customer)
                    .isEmpty();

        }
    }


    @Test
    void findAll() {

        customerRepository.save(customer1);
        customerRepository.save(customer2);
        customerRepository.save(customer3);
        entityManager.flush();

        List<Customer> customers = customerRepository.findAll();

        assertThat(customers)
                .isNotEmpty()
                .hasSize(3)
                .extracting(Customer::getFirstName)
                .containsExactlyInAnyOrder("alaa", "ahmed", "mo");




    }
}