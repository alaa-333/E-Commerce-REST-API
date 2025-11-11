package com.e_commerce.E_Commerce.REST.API.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable // mark class as a value obj not entity
public class Address {
    private String city;
    private String street;
    private String postalCode;
    private String country;
}
