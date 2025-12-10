package com.e_commerce.E_Commerce.REST.API.service;


import com.e_commerce.E_Commerce.REST.API.dto.request.PaginationRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.request.ProductCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.ProductUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.PaginationResponseDto;
import com.e_commerce.E_Commerce.REST.API.dto.response.ProductResponseDTO;
import com.e_commerce.E_Commerce.REST.API.exception.DuplicateResourceException;
import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;
import com.e_commerce.E_Commerce.REST.API.exception.product.ProductNotFoundException;
import com.e_commerce.E_Commerce.REST.API.exception.product.ProductQuantityExceedException;
import com.e_commerce.E_Commerce.REST.API.mapper.ProductMapper;
import com.e_commerce.E_Commerce.REST.API.model.Product;
import com.e_commerce.E_Commerce.REST.API.repository.ProductRepository;
import jakarta.validation.constraints.AssertTrue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Transactional
@RequiredArgsConstructor
@Service
public class ProductService {


    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    public ProductResponseDTO createProduct(ProductCreateRequestDTO requestDTO)
    {
        handlingInput(requestDTO);
        if (productRepository.existsByName(requestDTO.getName()))
        {
            throw  DuplicateResourceException.forProduct(requestDTO.getName());
        }
        if (!isPriceReasonable(requestDTO.getPrice()))
        {
            throw new ValidationException(ErrorCode.UNREASONABLE_PRICE);
        }
        validateStockQuantity(requestDTO.getStockQuantity());

        Product product = productMapper.toEntity(requestDTO);
        product.setActive(true);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(savedProduct);

    }




    public ProductResponseDTO getById(Long productId)
    {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        return productMapper.toResponseDTO(product);
    }

    public ProductResponseDTO updateProduct(ProductUpdateRequestDTO requestDTO,Long productId)
    {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        productMapper.updateEntityFromDTO(requestDTO,product);
        return productMapper
                .toResponseDTO(product);
    }

    public void deleteProduct(Long productId)
    {
        Product deletedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        productRepository.delete(deletedProduct);

    }


    public PaginationResponseDto<ProductResponseDTO> getAll(PaginationRequestDto requestDto)
    {
        PaginationRequestDto.validate(requestDto);
        Page<Product> productList = productRepository.findAll(
                requestDto.toPageable()
        );

        Page<ProductResponseDTO> responseDTOS = productList.map(productMapper::toResponseDTO);
        return PaginationResponseDto.PaginationMetadata.of(responseDTOS);
    }


    public PaginationResponseDto<ProductResponseDTO> getByRangePrice(BigDecimal min, BigDecimal max , PaginationRequestDto requestDto)
    {

        PaginationRequestDto.validate(requestDto);

        Page<Product> products = productRepository.findByPriceBetween(min, max , requestDto.toPageable());

       return PaginationResponseDto.PaginationMetadata.of(
               products.map(productMapper::toResponseDTO)
       );

    }

    public PaginationResponseDto<ProductResponseDTO> getProductsByName(String productName , PaginationRequestDto requestDto)
    {

        PaginationRequestDto.validate(requestDto);

        Page<Product> products =  productRepository.findByNameContaining(productName , requestDto.toPageable());

      return  PaginationResponseDto.PaginationMetadata.of(
              products.map(productMapper::toResponseDTO)
      );

    }

    public PaginationResponseDto<ProductResponseDTO> getProductsByCategory(
            String category,
            PaginationRequestDto requestDto
    )
    {
        PaginationRequestDto.validate(requestDto);

        Page<Product> products = productRepository.findByCategory(category, requestDto.toPageable());

        return PaginationResponseDto.PaginationMetadata.of(
                products.map(productMapper::toResponseDTO)
        );
    }


    // Custom validation methods
    public void handlingInput(ProductCreateRequestDTO requestDTO) {
        if (requestDTO.getName() !=  null) {
            requestDTO.setName(requestDTO.getName().trim());
        }
        if (requestDTO.getCategory() !=  null) {
            requestDTO.setCategory(requestDTO.getCategory().trim());
        }
        if (requestDTO.getDescription() !=  null) {
            requestDTO.setDescription(requestDTO.getDescription().trim());
        }
        if (requestDTO.getImgUrl() !=  null) {
            requestDTO.setImgUrl(requestDTO.getImgUrl().trim());
        }
    }

    public boolean isPriceReasonable(BigDecimal price) {
        return price != null && price.compareTo(new BigDecimal("1000000")) < 0; // Max 1 million
    }

    public void validateStockQuantity(Integer stockQuantity ) {
        if (stockQuantity != null && stockQuantity > 100000) {
            throw new ProductQuantityExceedException();
        }
    }

}
