package com.ecommerce.api.mapper;

import com.ecommerce.api.dto.request.product.CreateProductRequest;
import com.ecommerce.api.dto.request.product.UpdateProductRequest;
import com.ecommerce.api.dto.response.ProductResponse;
import com.ecommerce.api.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponse toProductResponse(Product product);

    Product toEntity(CreateProductRequest request);
    void updateEntity(Product product, UpdateProductRequest request);
}
