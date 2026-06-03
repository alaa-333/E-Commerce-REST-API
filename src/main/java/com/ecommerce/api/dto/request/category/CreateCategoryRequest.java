package com.ecommerce.api.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategoryRequest {

    @NotBlank(message = "Category name must not be blank")
    @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Category description must not be blank")
    @Size(min = 10, max = 200, message = "Category description must be between 2 and 200 characters")
    private String description;
}
