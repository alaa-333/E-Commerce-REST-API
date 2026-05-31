package com.ecommerce.api.dto.request.customer;

import com.ecommerce.api.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateCustomerRequest {

    private String phone;
    private Address address;
}
