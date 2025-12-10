package com.e_commerce.E_Commerce.REST.API.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@JsonInclude(JsonInclude.Include.NON_NULL)
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
        if (createdAt == null) {
            return "N/A";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        return createdAt.format(formatter);
    }

    public boolean hasOrders() {
        return totalOrders != null && totalOrders > 0;
    }
         }
