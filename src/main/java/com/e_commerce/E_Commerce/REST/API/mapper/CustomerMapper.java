package com.e_commerce.E_Commerce.REST.API.mapper;

import com.e_commerce.E_Commerce.REST.API.dto.request.AddressRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.CustomerCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.CustomerUpdateReqDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.AddressResponseDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.CustomerResponseDTO;
import com.e_commerce.E_Commerce.REST.API.model.Address;
import com.e_commerce.E_Commerce.REST.API.model.Customer;
import org.mapstruct.*;

import java.util.List;
import java.util.Optional;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CustomerMapper {

    // ======== REQUEST TO ENTITY =========
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "orderList", ignore = true)
    @Mapping(target = "address", source = "address")
    Customer toEntity(CustomerCreateRequestDTO requestDTO);

    // ======= ENTITY TO RESPONSE-DTO =======
    @Mapping(
            target = "totalOrders",
            expression = "java(mapTotalOrders(customer))"
    )
    @Mapping(target = "address", source = "address")
    CustomerResponseDTO toResponseDTO(Customer customer);

    // ======== ADDRESS MAPPINGS ============
    Address toEntity(AddressRequestDTO requestDTO);
    AddressResponseDTO toResponseDTO(Address address);

    // ======== PARTIAL UPDATE METHODS =======
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "orderList", ignore = true)
    void updateEntityFromDTO(CustomerUpdateReqDTO reqDTO, @MappingTarget Customer customer);

    // ======== CUSTOM MAPPING METHODS =======
    default Integer mapTotalOrders(Customer customer) {
        return Optional.ofNullable(customer.getOrderList())
                .map(List::size)
                .orElse(0);
    }

    default void updateAddressFromDTO(AddressRequestDTO requestDTO, @MappingTarget Address address) {
        if (requestDTO == null || address == null) {
            return;
        }

        Optional.ofNullable(requestDTO.getStreet())
                .ifPresent(address::setStreet);
        Optional.ofNullable(requestDTO.getCity())
                .ifPresent(address::setCity);
        Optional.ofNullable(requestDTO.getCountry())
                .ifPresent(address::setCountry);
        Optional.ofNullable(requestDTO.getPostalCode())
                .ifPresent(address::setPostalCode);
    }

    // ======== AFTER MAPPING CALLBACK =======
    @AfterMapping
    default void handleCustomerUpdate(
            @MappingTarget Customer customer,
            CustomerUpdateReqDTO reqDTO
    ) {
        if (reqDTO == null || customer == null) {
            return;
        }

        // Handle address update if present in DTO
        if (reqDTO.getAddress() != null) {
            Address address = customer.getAddress();
            if (address == null) {
                address = new Address();
                customer.setAddress(address);
            }
            updateAddressFromDTO(reqDTO.getAddress(), address);
        }
    }
}