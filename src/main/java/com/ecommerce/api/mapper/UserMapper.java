package com.ecommerce.api.mapper;


import com.ecommerce.api.dto.response.UserResponse;
import com.ecommerce.api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "email", source = "user.username")
    UserResponse toResponse(User user);

}
