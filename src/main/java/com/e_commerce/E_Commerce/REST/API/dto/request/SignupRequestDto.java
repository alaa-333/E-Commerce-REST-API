package com.e_commerce.E_Commerce.REST.API.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

    @JsonProperty("user")
    @Valid
    private UserCreateRequestDto userCreateRequestDto;

    @JsonProperty("customer")
    @Valid
    private CustomerCreateRequestDto customerCreateRequestDto;
}
