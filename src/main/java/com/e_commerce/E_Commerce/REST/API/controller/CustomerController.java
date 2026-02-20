package com.e_commerce.E_Commerce.REST.API.controller;

import com.e_commerce.E_Commerce.REST.API.dto.request.CustomerUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.PaginationRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.response.CustomerResponseDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.PaginationResponseDto;
import com.e_commerce.E_Commerce.REST.API.service.CustomerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomer(
            @PathVariable @NotNull(message = "id can be not null") @Positive(message = "id must be positive value") Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable @NotNull(message = "id can be not null") @Positive(message = "id must be positive value") Long id,
            @Valid @RequestBody CustomerUpdateRequestDTO updateReqDTO) {
        CustomerResponseDTO responseDTO = customerService.updateCustomer(id, updateReqDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<PaginationResponseDto<CustomerResponseDTO>> getAllCustomers(
            @Valid PaginationRequestDto requestDto) {

        PaginationResponseDto<CustomerResponseDTO> responseDTOList = customerService.getAllCustomers(requestDto);
        return ResponseEntity.ok(responseDTOList);
    }

}
