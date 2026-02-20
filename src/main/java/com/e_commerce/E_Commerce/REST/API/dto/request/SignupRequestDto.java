package com.e_commerce.E_Commerce.REST.API.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDTO {

    @JsonProperty("user")
    @Valid
    private UserCreateRequestDTO userCreateRequestDto;

    @JsonProperty("customer")
    @Valid
    private CustomerCreateRequestDTO customerCreateRequestDto;
}
