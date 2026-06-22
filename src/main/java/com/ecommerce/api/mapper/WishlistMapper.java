package com.ecommerce.api.mapper;

import com.ecommerce.api.dto.response.WishlistResponse;
import com.ecommerce.api.entity.Wishlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WishlistMapper {

    @Mapping(target = "customerId", source = "wishlist.customer.id")
    @Mapping(target = "totalItems", expression = "java(wishlist.getTotalItems())")
    WishlistResponse toWishlistResponse(Wishlist wishlist);
}

