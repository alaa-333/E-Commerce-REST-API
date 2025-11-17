package com.e_commerce.E_Commerce.REST.API.mapper;

import com.e_commerce.E_Commerce.REST.API.dto.request.AddressRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.CustomerCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.CustomerUpdateReqDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.AddressResponseDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.CustomerResponseDTO;
import com.e_commerce.E_Commerce.REST.API.model.Address;
import com.e_commerce.E_Commerce.REST.API.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    // ======== REQUEST TO ENTITY =========

    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "createdAt" , ignore = true)
    @Mapping(target = "orderList" , ignore = true)
    Customer toEntity(CustomerCreateRequestDTO requestDTO);

    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "createdAt" , ignore = true)
    @Mapping(target = "orderList" , ignore = true)
    @Mapping(target = "email" , ignore = true)
    void updateEntityFromDTO(CustomerUpdateReqDTO updateReqDTO , @MappingTarget Customer customer);


    // ======= Entity TO RESPONSE-DTO

    @Mapping(source = "listSize" , target = "totalOrders")
    @Mapping(target = "createdAt", source = "createdAt")
    CustomerResponseDTO toResponseDTO(Customer customer);

    // ======== ADDRESS ENTITY MAPPING ======

    Address toEntity(AddressRequestDTO requestDTO);
    AddressResponseDTO toResponseDTO(Address address);


    // helper method for partial updates
    default void updateCustomerFromDTO(CustomerUpdateReqDTO reqDTO , Customer customer)
    {
        if (reqDTO == null || customer == null)
                return;

        if (reqDTO.hasFirstName())
            customer.setFirstName(reqDTO.getFirstName());

        if (reqDTO.hasLastName())
            customer.setLastName(reqDTO.getLastName());

        if (reqDTO.hasPhone())
            customer.setPhone(reqDTO.getPhone());

        if (reqDTO.hasAddress())
             updateAddressFromDTO(reqDTO.getAddress() , customer.getAddress());

    }

    default void updateAddressFromDTO(AddressRequestDTO requestDTO , Address address)
    {
        if (requestDTO.getCity() != null && !requestDTO.getCity().isBlank())
                address.setCity(requestDTO.getCity());

        if (requestDTO.getCountry() != null && !requestDTO.getCountry().isBlank())
                address.setCountry(requestDTO.getCountry());

        if (requestDTO.getPostalCode() != null && !requestDTO.getPostalCode().isBlank())
                address.setPostalCode(requestDTO.getPostalCode());

        if (requestDTO.getStreet() != null && !requestDTO.getStreet().isBlank())
                address.setStreet(requestDTO.getStreet());

    }

}

