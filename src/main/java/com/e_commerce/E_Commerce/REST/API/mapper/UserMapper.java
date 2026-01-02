package com.e_commerce.E_Commerce.REST.API.mapper;

import com.e_commerce.E_Commerce.REST.API.dto.request.UserCreateRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.request.UserUpdateRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.response.UserResponse;
import com.e_commerce.E_Commerce.REST.API.model.User;
import com.e_commerce.E_Commerce.REST.API.model.enums.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Mapper(componentModel = "spring", imports = { Role.class, Set.class })
public interface UserMapper {

    @Mapping(target = "roles", source = ".", qualifiedByName = "setDefaultRoles")
    User toEntity(UserCreateRequestDto requestDto);

    @Named("setDefaultRoles")
    default Set<Role> setDefaultRoles(UserCreateRequestDto requestDto) {
        return Set.of(Role.ROLE_USER);
    }

    default User updateUserFromDto(User user , UserUpdateRequestDto requestDto)
    {
        Optional.ofNullable(requestDto.getEmail())
                .ifPresent(user::setEmail);
        Optional.ofNullable(requestDto.getPassword())
                .ifPresent(user::setPassword);
        Optional.ofNullable(requestDto.getEnabled())
                .ifPresent(user::setEnabled);
        Optional.ofNullable(requestDto.getAccountNonLocked())
                .ifPresent(user::setAccountNonLocked);

        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }


    UserResponse toResponse(User user);
}