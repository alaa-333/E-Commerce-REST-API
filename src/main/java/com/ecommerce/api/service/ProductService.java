package com.ecommerce.api.service;

import com.ecommerce.api.dto.request.product.CreateProductRequest;
import com.ecommerce.api.dto.request.product.ProductSearchCriteria;
import com.ecommerce.api.dto.request.product.UpdateProductRequest;
import com.ecommerce.api.dto.response.PagedResponse;
import com.ecommerce.api.dto.response.ProductResponse;
import com.ecommerce.api.entity.Product;
import com.ecommerce.api.exception.EcommerceAppException;
import com.ecommerce.api.exception.ErrorCode;
import com.ecommerce.api.exception.ResourceNotFoundException;
import com.ecommerce.api.mapper.ProductMapper;
import com.ecommerce.api.repository.CategoryRepository;
import com.ecommerce.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    public PagedResponse<ProductResponse> getAllProducts(PageRequest pageRequest) {

        var pageResponse = productRepository.findAll(pageRequest)
                .map(productMapper::toProductResponse);

        return PagedResponse.from(pageResponse);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ProductResponse getProduct(Long id) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND ,"Product with id " + id + " not found"));

        return productMapper.toProductResponse(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND ,"Category with id " + request.getCategoryId() + " not found"));

        var productEntity = productMapper.toEntity(request);
        productEntity.setCategory(category);
        productEntity.setActive(true);
        var savedProduct = productRepository.save(productEntity);

        category.increaseProductCount();
        categoryRepository.save(category);

        return productMapper.toProductResponse(savedProduct);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void updateProduct(Long id, UpdateProductRequest updateRequest) {

        var product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND ,"Product with id " + id + " not found"));

        productMapper.updateEntity(product, updateRequest);

         productRepository.save(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteProduct(Long id) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND ,"Product with id " + id + " not found"));
        product.setActive(false);
        productRepository.save(product);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public PagedResponse<ProductResponse> searchProducts(ProductSearchCriteria criteria, PageRequest pageRequest) {

        var specificationOfProduct = ProductSpecificationBuilder.buildSpecification(criteria);

        var response = productRepository.findAll(specificationOfProduct, pageRequest)
                .map(productMapper::toProductResponse);
        return PagedResponse.from(response);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void reduceStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "product not found"));

        if (product.getStockQuantity() < quantity) {
            throw new EcommerceAppException(ErrorCode.PRODUCT_INSUFFICIENT_STOCK,
                    "Requested: " + quantity + ", Available: " + product.getStockQuantity());
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);

        if (product.getStockQuantity() < 10) {
            log.warn("LOW STOCK: Product {} has only {} units remaining",
                    productId, product.getStockQuantity());
        }

        productRepository.save(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void restoreStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "product not found for product "+productId));
        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepository.save(product);
    }
}
