package com.e_commerce.E_Commerce.REST.API.controller;

import com.e_commerce.E_Commerce.REST.API.dto.request.CustomerCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.CustomerUpdateReqDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.CustomerResponseDTO;
import com.e_commerce.E_Commerce.REST.API.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;


    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerCreateRequestDTO requestDTO)
    {
        CustomerResponseDTO response = customerService.createCustomer(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public CustomerResponseDTO getCustomer(@PathVariable Long id)
    {
        return customerService.getCustomerById(id);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<CustomerResponseDTO> getCustomerByEmail(@PathVariable String email)
    {
        CustomerResponseDTO responseDTO = customerService.getCustomerByEmail(email);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer (@PathVariable Long id,
                                                               @Valid @RequestBody  CustomerUpdateReqDTO updateReqDTO)
    {
        CustomerResponseDTO responseDTO = customerService.updateCustomer(id,updateReqDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers()
    {
        List<CustomerResponseDTO> responseDTOList = customerService.getAllCustomers();
        return  ResponseEntity.ok(responseDTOList);
    }








}
