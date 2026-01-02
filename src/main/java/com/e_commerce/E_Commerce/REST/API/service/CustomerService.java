package com.e_commerce.E_Commerce.REST.API.service;

import com.e_commerce.E_Commerce.REST.API.dto.request.CustomerCreateRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.request.CustomerUpdateReqDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.PaginationRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.response.CustomerResponseDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.PaginationResponseDto;
import com.e_commerce.E_Commerce.REST.API.exception.DuplicateResourceException;
import com.e_commerce.E_Commerce.REST.API.exception.customer.CustomerNotFoundException;
import com.e_commerce.E_Commerce.REST.API.mapper.CustomerMapper;
import com.e_commerce.E_Commerce.REST.API.model.Customer;
import com.e_commerce.E_Commerce.REST.API.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Transactional
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;



    //  CREATE NEW CUSTOMER OBJ
    public CustomerResponseDTO createCustomer(CustomerCreateRequestDto requestDTO) {

        removeSpaces(requestDTO);
//        if (customerRepository.existsByEmail(requestDTO.getEmail()))
//        {
//            throw DuplicateResourceException.forCustomer(requestDTO.getEmail());
//        }


        Customer customer = customerMapper.toEntity(requestDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponseDTO(savedCustomer);

    }


    // GET CUSTOMER BY ID
    public CustomerResponseDTO getCustomerById(Long id)
    {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new  CustomerNotFoundException(id));

        return customerMapper.toResponseDTO(customer);
    }

    // GET CUSTOMER BY EMAIL
//    public CustomerResponseDTO getCustomerByEmail(String email)
//    {
//        Customer customer = customerRepository.findByEmail(email)
//                .orElseThrow(() -> new CustomerNotFoundException(email));
//
//        return customerMapper.toResponseDTO(customer);
//    }

    //  UPDATE CUSTOMER
    public CustomerResponseDTO updateCustomer(Long id , CustomerUpdateReqDTO updateReqDTO)
    {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new  CustomerNotFoundException(id));


        customerMapper.updateEntityFromDTO(updateReqDTO,customer);
        Customer updatedCustomer = customerRepository.save(customer);

        return customerMapper.toResponseDTO(updatedCustomer);
    }


    //  GET ALL CUSTOMER
    public PaginationResponseDto<CustomerResponseDTO> getAllCustomers(PaginationRequestDto paginationDto)
    {
        // validate apiField & check from my whiteList
        PaginationRequestDto.validate(paginationDto);

       Page<Customer> customerPage = customerRepository.findAll(
               paginationDto.toPageable()
       );
       Page<CustomerResponseDTO> responseDTOS = customerPage
               .map(customerMapper::toResponseDTO);
       return  PaginationResponseDto.PaginationMetadata.of(responseDTOS);
    }



    private void removeSpaces(CustomerCreateRequestDto requestDTO)
    {
        if (requestDTO.getFirstName() != null)
        {
            requestDTO.setFirstName(requestDTO.getFirstName().trim());
        }
        if (requestDTO.getLastName() != null)
        {
            requestDTO.setLastName(requestDTO.getLastName().trim());
        }

    }








}
