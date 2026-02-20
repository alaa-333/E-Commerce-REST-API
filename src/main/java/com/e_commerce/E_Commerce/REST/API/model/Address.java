package com.e_commerce.E_Commerce.REST.API.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable // mark class as a value obj not entity
public class Address {
    private String city;
    private String street;
    private String postalCode;
    private String country;
}
