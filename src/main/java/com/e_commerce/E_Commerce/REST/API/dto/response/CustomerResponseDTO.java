package com.e_commerce.E_Commerce.REST.API.dto.response;

import java.time.LocalDateTime;

public record CustomerResponseDTO
        (
                Long id,
                String firstName,
                String lastName,
                String email,
                String phone,
                AddressResponseDTO address,
                LocalDateTime createdAt,
                Integer totalOrders
        ) {

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getFormattedCreatedAt() {
        return createdAt != null ? createdAt.toString() : "N/A";
    }

    public boolean hasOrders() {
        return totalOrders != null && totalOrders > 0;
    }
         }
