package com.e_commerce.E_Commerce.REST.API.service;

import com.e_commerce.E_Commerce.REST.API.dto.request.CustomerUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.PaginationRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.response.CustomerResponseDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.PaginationResponseDto;
import com.e_commerce.E_Commerce.REST.API.exception.customer.CustomerNotFoundException;
import com.e_commerce.E_Commerce.REST.API.mapper.CustomerMapper;
import com.e_commerce.E_Commerce.REST.API.model.Customer;
import com.e_commerce.E_Commerce.REST.API.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public PaginationResponseDto<CustomerResponseDTO> getAllCustomers(PaginationRequestDto paginationRequestDto) {
        Pageable pageable = PageRequest.of(
                paginationRequestDto.getPage(),
                paginationRequestDto.getSize(),
                Sort.by(paginationRequestDto.getSortDirection(), paginationRequestDto.getSortBy()));
        Page<Customer> page = customerRepository.findAll(pageable);
        List<CustomerResponseDTO> content = page.getContent().stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList());

        PaginationResponseDto.PaginationMetadata metadata = new PaginationResponseDto.PaginationMetadata(
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious(),
                page.isFirst(),
                page.isLast());
        return new PaginationResponseDto<>(content, metadata);
    }

    public CustomerResponseDTO getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(customerMapper::toResponse)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public CustomerResponseDTO updateCustomer(Long id, CustomerUpdateRequestDTO customerUpdateReqDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        customerMapper.updateCustomerFromDto(customerUpdateReqDTO, customer);
        Customer updatedCustomer = customerRepository.save(customer);

        return customerMapper.toResponse(updatedCustomer);
    }

}
