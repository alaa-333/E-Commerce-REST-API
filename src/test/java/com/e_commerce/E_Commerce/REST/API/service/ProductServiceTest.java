package com.e_commerce.E_Commerce.REST.API.service;

import com.e_commerce.E_Commerce.REST.API.mapper.ProductMapper;
import com.e_commerce.E_Commerce.REST.API.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private  ProductRepository productRepository;
    @Mock
    private  ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct() {
    }

    @Test
    void getById() {
    }

    @Test
    void updateProduct() {
    }

    @Test
    void deleteProduct() {
    }

    @Test
    void getAll() {
    }
}