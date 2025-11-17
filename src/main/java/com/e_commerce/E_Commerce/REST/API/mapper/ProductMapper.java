package com.e_commerce.E_Commerce.REST.API.mapper;

import com.e_commerce.E_Commerce.REST.API.dto.request.ProductCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.ProductUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.ProductResponseDTO;
import com.e_commerce.E_Commerce.REST.API.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // ======== to Entity ==========

    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "active" , ignore = true)
    @Mapping(target = "itemList" , ignore = true)
    Product toEntity(ProductCreateRequestDTO requestDTO);


    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "itemList" , ignore = true)
    void updateEntityFromDTO(ProductUpdateRequestDTO updateRequestDTO , @MappingTarget Product product);

    // ===== entity to response Dto
    @Mapping(source = "itemList" , target = "totalOrders")
    ProductResponseDTO toResponseDTO(Product product);

    // update only provided fields

    default void updateProductFromDTO(ProductUpdateRequestDTO requestDTO , Product product)
    {
        if (requestDTO.hasName())
                product.setName(requestDTO.getName());

        if (requestDTO.hasDescription())
                product.setDescription(requestDTO.getDescription());

        if (requestDTO.hasActive())
                product.setActive(requestDTO.getActive());

        if (requestDTO.hasCategory())
                product.setCategory(requestDTO.getCategory());

        if (requestDTO.hasImgUrl())
                product.setImgUrl(requestDTO.getImgUrl());
    }

}
