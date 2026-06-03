package com.ecommerce.api.mapper;

import com.ecommerce.api.dto.response.CategoryResponse;
import com.ecommerce.api.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toCategoryResponse(Category savedCategory);
}
