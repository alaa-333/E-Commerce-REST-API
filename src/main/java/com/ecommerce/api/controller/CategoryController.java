package com.ecommerce.api.controller;


import com.ecommerce.api.dto.request.category.CreateCategoryRequest;
import com.ecommerce.api.dto.response.ApiResponse;
import com.ecommerce.api.dto.response.CategoryResponse;
import com.ecommerce.api.dto.response.PagedResponse;
import com.ecommerce.api.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<CategoryResponse>>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
         ){

        var response = categoryService.getCategories(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        var response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @RequestBody @Valid CreateCategoryRequest request
            ) {

        var response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("category created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateCategory(
            @PathVariable(name = "id") Long categoryId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description
    ) {

        var responseStaus = categoryService.updateCategory(categoryId, name, description);

        if (!responseStaus) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("category update failed")
            );
        }

        return ResponseEntity.ok(ApiResponse.success("category updated successfully"));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(
            @PathVariable Long id
    ) {
        var responseStatus = categoryService.deleteCategory(id);
        if (!responseStatus) {
            return ResponseEntity.badRequest().body(ApiResponse.error("category deletion failed"));
        }
        return ResponseEntity.ok(ApiResponse.success("category deleted successfully"));
    }
}
