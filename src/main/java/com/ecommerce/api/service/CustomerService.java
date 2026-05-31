package com.ecommerce.api.service;


import com.ecommerce.api.dto.request.customer.CreateCustomerRequest;
import com.ecommerce.api.dto.request.customer.UpdateCustomerRequest;
import com.ecommerce.api.dto.response.CustomerResponse;
import com.ecommerce.api.dto.response.PagedResponse;

import com.ecommerce.api.entity.User;
import com.ecommerce.api.exception.ErrorCode;
import com.ecommerce.api.exception.ResourceNotFoundException;
import com.ecommerce.api.mapper.CustomerMapper;
import com.ecommerce.api.repository.CustomerRepository;
import com.ecommerce.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final CustomerMapper mapper;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN') or #request.userId ==  authentication.principal.id")
    public CustomerResponse createNewCustomer(CreateCustomerRequest request) {


        User user = userRepository.findById(
                request.getUserId()
        ).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND, "user profile not found for customer with id "+request.getUserId()));

        var customer = mapper.toEntity(request);
        customer.setUser(user);
        var savedCustomer = customerRepository.save(
                customer
        );

        user.setCustomer(savedCustomer);

        return mapper.toResponse(savedCustomer);
    }



    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public PagedResponse<CustomerResponse> getAll(int page, int size) {

        var pageRequest = PageRequest.of(page, size);
        var pageResponse = customerRepository.findAll(pageRequest)
                .map(mapper::toResponse);

        return PagedResponse.from(pageResponse);

    }


    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN') or #id ==  authentication.principal.id")
    public CustomerResponse getCustomer(Long id) {

        var customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CUSTOMER_NOT_FOUND, "customer not found with id: " + id));

        return mapper.toResponse(customer);
    }


    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN') or #id ==  authentication.principal.id")
    public boolean updateCustomer(Long id , UpdateCustomerRequest request) {
        var customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CUSTOMER_NOT_FOUND, "customer not found with id: " + id));

        customer.setPhone(request.getPhone() != null ? request.getPhone() : customer.getPhone());
        if (request.getAddress() != null) {
            customer.setAddress(request.getAddress());
        }
        var effectedRows = customerRepository.updatePhoneAndAddressById(id, request.getPhone(), request.getAddress());

        var success = effectedRows > 0;

        if (!success) {
            log.warn("failed to update customer with id: {}", id);
            return false;
        }

        log.info("customer updated with id: {}", id);
        return true;

    }
}
