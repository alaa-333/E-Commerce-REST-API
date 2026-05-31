package com.ecommerce.api.controller;


import com.ecommerce.api.dto.request.customer.CreateCustomerRequest;
import com.ecommerce.api.dto.request.customer.UpdateCustomerRequest;
import com.ecommerce.api.dto.response.ApiResponse;
import com.ecommerce.api.dto.response.CustomerResponse;
import com.ecommerce.api.dto.response.PagedResponse;
import com.ecommerce.api.service.CustomerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;


    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @RequestBody @Valid CreateCustomerRequest request
            ) {

        var response = customerService.createNewCustomer(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("customer created successfully", response)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<CustomerResponse>>> getAllCustomers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        var response = customerService.getAll(page, size);
        return ResponseEntity.ok(
                ApiResponse.success(response)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(
            @PathVariable
            @Positive(message = "id must be a positive value")
            @NotNull(message = "id must be not null") Long id
    ) {
        var response = customerService.getCustomer(id);
        return ResponseEntity.ok(
                ApiResponse.success(response)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateCustomer(
            @PathVariable
            @Positive(message = "id must be a positive value")
            @NotNull(message = "id must be not null") Long id,
            @RequestBody @Valid UpdateCustomerRequest request
    ) {
        var responseStatus = customerService.updateCustomer(id, request);
        if (!responseStatus) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("failed to update customer")
            );
        }

        return ResponseEntity.ok(
                ApiResponse.success("User updated successfully")
        );

    }


}
