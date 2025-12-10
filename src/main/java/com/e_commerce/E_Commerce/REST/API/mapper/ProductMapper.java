package com.e_commerce.E_Commerce.REST.API.mapper;

import com.e_commerce.E_Commerce.REST.API.dto.request.ProductCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.ProductUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.ProductResponseDTO;
import com.e_commerce.E_Commerce.REST.API.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

    // ======== to Entity ==========
    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "active" , ignore = true)
    @Mapping(target = "itemList" , ignore = true)
    @Mapping(target = "createdAt" , ignore = true)
    Product toEntity(ProductCreateRequestDTO requestDTO);

    // ===== entity to response Dto
    @Mapping(target = "totalOrders", expression = "java(product.getItemList() != null ? product.getItemList().size() : 0)")
    ProductResponseDTO toResponseDTO(Product product);


    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "itemList" , ignore = true)
    void updateEntityFromDTO(ProductUpdateRequestDTO updateRequestDTO , @MappingTarget Product product);



}
