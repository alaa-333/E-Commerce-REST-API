package com.e_commerce.E_Commerce.REST.API.controller;


import com.e_commerce.E_Commerce.REST.API.dto.request.PaginationRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.request.ProductCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.ProductUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.PaginationResponseDto;
import com.e_commerce.E_Commerce.REST.API.dto.response.ProductResponseDTO;
import com.e_commerce.E_Commerce.REST.API.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
@Validated
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductCreateRequestDTO requestDTO)
    {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(requestDTO));
    }

    @GetMapping("/{id}")
    public ProductResponseDTO getById(
            @PathVariable("id")
            @NotNull(message = "Id must not be null")
            @Positive(message = "Id must be a positive number")
            Long id
    )
    {
        return productService.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @Valid @RequestBody ProductUpdateRequestDTO requestDTO,
            @PathVariable("id")
            @NotNull(message = "id must be not null")
            @Positive(message = "id must be a positive number")
            Long id
            )
    {
        return ResponseEntity.ok(productService.updateProduct(requestDTO,id));
    }

    @GetMapping
    public PaginationResponseDto<ProductResponseDTO> getAll(
            @Valid PaginationRequestDto requestDto
            )
    {
        return productService.getAll(requestDto);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id")
            @NotNull(message = "id must be not null")
            @Positive(message = "id must be positive number")
            Long id
    )
    {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/price")
    public PaginationResponseDto<ProductResponseDTO> filteredProductsByPrice(
            @RequestParam("minPrice") @PositiveOrZero BigDecimal min,
            @RequestParam("maxPrice") @Positive BigDecimal max,
            @Valid PaginationRequestDto requestDto
    )
    {
        return productService
                .getByRangePrice(min, max , requestDto);
    }


    @GetMapping("/name")
    public PaginationResponseDto<ProductResponseDTO> filteredProductsByName(
            @RequestParam @NotNull(message = "name must be not null") String name,
            @Valid PaginationRequestDto requestDto
    )
    {
        return productService
                .getProductsByName(name ,requestDto);
    }

    @GetMapping("/category")
    public PaginationResponseDto<ProductResponseDTO> getByCategory(
            @RequestParam @NotNull(message = "category must be not null") String category,
            @Valid PaginationRequestDto requestDto
    )
    {
        return productService.getProductsByCategory(category , requestDto);
    }



}
