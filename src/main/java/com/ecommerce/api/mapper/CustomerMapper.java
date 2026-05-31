package com.ecommerce.api.mapper;

import com.ecommerce.api.dto.request.customer.CreateCustomerRequest;
import com.ecommerce.api.dto.response.CustomerResponse;
import com.ecommerce.api.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer toEntity(CreateCustomerRequest request);
    CustomerResponse toResponse(Customer customer);
}
