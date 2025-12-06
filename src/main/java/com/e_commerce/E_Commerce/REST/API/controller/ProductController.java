package com.e_commerce.E_Commerce.REST.API.controller;


import com.e_commerce.E_Commerce.REST.API.dto.request.ProductCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.ProductUpdateRequestDTO;
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
@RequestMapping("/api/products")
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
    public ProductResponseDTO updateProduct(
            @Valid @RequestBody ProductUpdateRequestDTO requestDTO,
            @PathVariable("id")
            @NotNull(message = "id must be not null")
            @Positive(message = "id must be a positive number")
            Long id
            )
    {
        return productService.updateProduct(requestDTO,id);
    }

    @GetMapping
    public List<ProductResponseDTO> getAll()
    {
        return productService.getAll();
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

    @GetMapping
    public List<ProductResponseDTO> filteredProductsByPrice(
            @RequestParam("minPrice") @PositiveOrZero BigDecimal min,
            @RequestParam("maxPrice") @Positive BigDecimal max
    )
    {
        return productService
                .getByRangePrice(min, max);
    }
    @GetMapping("/search")
    public List<ProductResponseDTO> filteredProductsByName(
            @RequestParam @NotNull(message = "name must be not null") String name
    )
    {
        return productService
                .getProductsByName(name);
    }



}
