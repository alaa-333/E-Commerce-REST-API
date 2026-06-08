package com.ecommerce.api.controller;

import com.ecommerce.api.dto.request.product.CreateProductRequest;
import com.ecommerce.api.dto.request.product.ProductSearchCriteria;
import com.ecommerce.api.dto.request.product.UpdateProductRequest;
import com.ecommerce.api.dto.response.ApiResponse;
import com.ecommerce.api.dto.response.PagedResponse;
import com.ecommerce.api.dto.response.ProductResponse;
import com.ecommerce.api.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Validated
public class ProductController {
    private final ProductService productService;


    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponse>>> getAllProducts(
            @RequestParam(value = "page" , defaultValue = "0") int page,
            @RequestParam(value = "size" , defaultValue = "10") int size

    ) {
        // Placeholder for actual implementation
        var pageRequest = PageRequest.of(page, size);
        var pageResponse = productService.getAllProducts(pageRequest);
        return ResponseEntity.ok(
                ApiResponse.success(pageResponse)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @PathVariable @Positive @NotNull Long id
    ) {

        var response = productService.getProduct(id);
        return ResponseEntity.ok(
                ApiResponse.success(response)
        );

    }

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @RequestBody @Valid CreateProductRequest request
            ) {
        var productResponse = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(productResponse)
                );

    }

    @PutMapping("/{id")
    public ResponseEntity<ApiResponse<String>> updateProduct
            (
                 @PathVariable  @Positive @NotNull Long id,
                 @Valid @RequestBody UpdateProductRequest updateRequest
            ) {

            productService.updateProduct(id, updateRequest);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(
                            ApiResponse.success("product updated successfully")
                    );

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable @Positive @NotNull Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("product deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponse>>> searchProducts(
            @RequestBody @Valid ProductSearchCriteria criteria,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        var response = productService.searchProducts(criteria, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
        return ResponseEntity.ok(ApiResponse.success(response));
    }


}
