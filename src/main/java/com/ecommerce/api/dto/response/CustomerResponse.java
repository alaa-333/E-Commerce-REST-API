package com.ecommerce.api.dto.response;

import com.ecommerce.api.entity.Address;


public record CustomerResponse(

        String phone,
        Address address
) {
}
