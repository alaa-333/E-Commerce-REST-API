package com.ecommerce.api.mapper;

import com.ecommerce.api.dto.response.CartResponse;
import com.ecommerce.api.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "items", source = "cart.cartItems")
    @Mapping(target = "totalAmount", expression = "java(cart.getTotalAmount())")
    @Mapping(target = "totalItems", expression = "java(cart.getTotalItems())")
    CartResponse toCartResponse(Cart cart);
}
