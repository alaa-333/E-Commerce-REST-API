package com.e_commerce.E_Commerce.REST.API.mapper;

import com.e_commerce.E_Commerce.REST.API.dto.request.AddressRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.CustomerCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.CustomerUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.AddressResponseDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.CustomerResponseDTO;
import com.e_commerce.E_Commerce.REST.API.model.Address;
import com.e_commerce.E_Commerce.REST.API.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orderList", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "address", source = "address")
    Customer toEntity(CustomerCreateRequestDTO requestDto);

    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "totalOrders", expression = "java(customer.getOrderList() != null ? customer.getOrderList().size() : 0)")
    CustomerResponseDTO toResponse(Customer customer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orderList", ignore = true)
    Customer toEntity(CustomerResponseDTO responseDto);

    @Mapping(target = "city", source = "city")
    @Mapping(target = "street", source = "street")
    @Mapping(target = "postalCode", source = "postalCode")
    @Mapping(target = "country", source = "country")
    Address toEntity(AddressRequestDTO requestDto);

    @Mapping(target = "city", source = "city")
    @Mapping(target = "street", source = "street")
    @Mapping(target = "postalCode", source = "postalCode")
    @Mapping(target = "country", source = "country")
    AddressResponseDTO toResponse(Address address);

    default void updateCustomerFromDto(CustomerUpdateRequestDTO requestDto, @MappingTarget Customer customer) {
        Optional.ofNullable(requestDto.getFirstName())
                .ifPresent(customer::setFirstName);
        Optional.ofNullable(requestDto.getLastName())
                .ifPresent(customer::setLastName);
        Optional.ofNullable(requestDto.getPhone())
                .ifPresent(customer::setPhone);

        if (requestDto.getAddress() != null) {
            if (customer.getAddress() == null) {
                customer.setAddress(new Address());
            }
            updateAddressFromDto(requestDto.getAddress(), customer.getAddress());
        }
    }

    default void updateAddressFromDto(AddressRequestDTO requestDto, @MappingTarget Address address) {
        if (address == null)
            return;

        Optional.ofNullable(requestDto.getStreet())
                .ifPresent(address::setStreet);
        Optional.ofNullable(requestDto.getCity())
                .ifPresent(address::setCity);
        Optional.ofNullable(requestDto.getCountry())
                .ifPresent(address::setCountry);
        Optional.ofNullable(requestDto.getPostalCode())
                .ifPresent(address::setPostalCode);
    }
}