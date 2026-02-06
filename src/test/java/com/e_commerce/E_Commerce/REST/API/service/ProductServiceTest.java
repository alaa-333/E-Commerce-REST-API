package com.e_commerce.E_Commerce.REST.API.service;

import com.e_commerce.E_Commerce.REST.API.dto.request.PaginationRequestDto;
import com.e_commerce.E_Commerce.REST.API.dto.request.ProductCreateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.request.ProductUpdateRequestDTO;
import com.e_commerce.E_Commerce.REST.API.dto.response.ProductResponseDTO;
import com.e_commerce.E_Commerce.REST.API.exception.DuplicateResourceException;
import com.e_commerce.E_Commerce.REST.API.exception.ErrorCode;
import com.e_commerce.E_Commerce.REST.API.exception.ValidationException;
import com.e_commerce.E_Commerce.REST.API.exception.product.ProductNotFoundException;
import com.e_commerce.E_Commerce.REST.API.exception.product.ProductQuantityExceedException;
import com.e_commerce.E_Commerce.REST.API.mapper.ProductMapper;
import com.e_commerce.E_Commerce.REST.API.model.Product;
import com.e_commerce.E_Commerce.REST.API.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private  ProductRepository productRepository;
    @Mock
    private  ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;


    // ================== createProduct ===================
    @Test
    void createProduct_whenProductDoesNotExists_shouldBeCreated() {

        String productName = "sony headphone";
        BigDecimal productPrice = new BigDecimal(1000);
        int productQuantity = 10;

        ProductCreateRequestDTO dto = new ProductCreateRequestDTO();
        dto.setName(productName);
        dto.setPrice(productPrice);
        dto.setStockQuantity(productQuantity);

        Product productEntity = new Product();
        productEntity.setName(productName);
        productEntity.setPrice(productPrice);
        productEntity.setStockQuantity(productQuantity);

        Product savedProduct = new Product();
        savedProduct.setId(1L); // Assuming it gets an ID after save
        savedProduct.setName(productName);
        savedProduct.setPrice(productPrice);
        savedProduct.setStockQuantity(productQuantity);
        savedProduct.setActive(true);

        ProductResponseDTO expectedResponse = mock(ProductResponseDTO.class);
        when(expectedResponse.id()).thenReturn(savedProduct.getId());
        when(expectedResponse.name()).thenReturn(savedProduct.getName());
        when(expectedResponse.price()).thenReturn(savedProduct.getPrice());
        when(expectedResponse.stockQuantity()).thenReturn(savedProduct.getStockQuantity());


        //stubbing
        when(productRepository.existsByName(dto.getName())).thenReturn(false);
        when(productMapper.toEntity(dto)).thenReturn(productEntity);
        when(productRepository.save(productEntity)).thenReturn(savedProduct);
        when(productMapper.toResponseDTO(savedProduct)).thenReturn(expectedResponse);

        // act
        ProductResponseDTO actualResponse = productService.createProduct(dto);

        // assert
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.name()).isEqualTo(savedProduct.getName());
        assertThat(actualResponse.price()).isEqualTo(savedProduct.getPrice());
        assertThat(actualResponse.stockQuantity()).isEqualTo(savedProduct.getStockQuantity());

        // verify
        verify(productRepository).existsByName(productName);
        verify(productRepository).save(argThat( product ->

                product.isActive() && product.getName().equals(productName)

                ));

        verify(productMapper).toEntity(dto);
        verify(productMapper).toResponseDTO(savedProduct);
    }

    @Test
    void createProduct_whenProductExists_shouldThrowDuplicateResourceException() {

        String productName = "sony headphone";
        ProductCreateRequestDTO dto = new ProductCreateRequestDTO();
        dto.setName(productName);

        //stubbing
        when(productRepository.existsByName(dto.getName())).thenReturn(true);


        // act + assert
        assertThatThrownBy(() -> productService.createProduct(dto))
                .isInstanceOf(DuplicateResourceException.class)
                        .hasMessage("A product with this name/code already exists");


        // verify
        verify(productRepository).existsByName(productName);
        verify(productRepository, never()).save(any(Product.class));
        verifyNoInteractions(productMapper);

    }

    @Test
    void createProduct_whenPriceIsUnreasonable_shouldThrowValidationException() {
        // Arrange
        String productName = "sony headphone";
        BigDecimal unreasonablePrice = new BigDecimal(1000000);

        ProductCreateRequestDTO dto = new ProductCreateRequestDTO();
        dto.setName(productName);
        dto.setPrice(unreasonablePrice);
        dto.setStockQuantity(10);

        when(productRepository.existsByName(productName)).thenReturn(false);

        // act , assert
        assertThatThrownBy(() -> productService.createProduct(dto))
                .isInstanceOf(ValidationException.class)
                .satisfies(ex -> {
                    ValidationException validationEx = (ValidationException) ex;
                    assertThat(validationEx.getErrorCode()).isEqualTo(ErrorCode.UNREASONABLE_PRICE);
                });

        // verify
        verify(productRepository).existsByName(productName);
        verify(productRepository, never()).save(any(Product.class));
        verifyNoInteractions(productMapper);
    }
    @Test
    void createProduct_whenQuantityExceedsLimit_shouldThrowProductQuantityExceedException() {
        // arrange
        String productName = "sony headphone";
        BigDecimal productPrice = new BigDecimal(1000);
        Integer excessiveQuantity = 150000;

        ProductCreateRequestDTO dto = new ProductCreateRequestDTO();
        dto.setName(productName);
        dto.setPrice(productPrice);
        dto.setStockQuantity(excessiveQuantity);

        when(productRepository.existsByName(productName)).thenReturn(false);

        // act & assert
        assertThatThrownBy(() -> productService.createProduct(dto))
                .isInstanceOf(ProductQuantityExceedException.class)
                .hasMessageContaining("cannot exceed");

        // verify
        verify(productRepository).existsByName(productName);
        verify(productRepository, never()).save(any(Product.class));
        verifyNoInteractions(productMapper);
    }




    // ================== getById ===================
    @Test
    void getById_whenProductExists_shouldReturnProduct() {

        Long productId = 2L;
        Product expectedProduct = new Product();
        expectedProduct.setId(productId);

        ProductResponseDTO expectedResponse = mock(ProductResponseDTO.class);
        when(expectedResponse.id()).thenReturn(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(expectedProduct));
        when(productMapper.toResponseDTO(expectedProduct)).thenReturn(expectedResponse);

        // act
        ProductResponseDTO actualResponse = productService.getById(productId);

        // assert
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.id()).isEqualTo(expectedProduct.getId());

        // VERIFY
        verify(productRepository).findById(productId);
        verify(productRepository,never()).save(any());
        verify(productMapper).toResponseDTO(expectedProduct);

    }

    @Test
    void getById_whenProductDoesNotExists_shouldThrowException() {

        // arrange
        Long productId = 2L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // act
        assertThatThrownBy(() -> productService.getById(productId))
                .isInstanceOf(ProductNotFoundException.class)
                        .hasMessage("Product not found");

        // VERIFY
        verify(productRepository).findById(productId);
        verify(productRepository,never()).save(any());
        verifyNoInteractions(productMapper);

    }





    // ================== updateProduct ===================
    @Test
    void updateProduct_whenProductExist_shouldBeUpdated() {

        Long productId = 2L;
        String productName = "JPL headphone";
        BigDecimal productPrice = new BigDecimal(200);


        ProductUpdateRequestDTO incomingRequestDTO = new ProductUpdateRequestDTO();
        incomingRequestDTO.setName(productName);
        incomingRequestDTO.setPrice(productPrice);

        Product productEntity = new Product();
        productEntity.setId(productId);
        productEntity.setName("sony headphone");
        productEntity.setPrice(productPrice);

        Product savedProduct = new Product();
        savedProduct.setName(productName);
        savedProduct.setPrice(productPrice);
        savedProduct.setId(productId);

        ProductResponseDTO expectedResponse = mock(ProductResponseDTO.class);
        when(expectedResponse.id()).thenReturn(productId);
        when(expectedResponse.name()).thenReturn(productName);
        when(expectedResponse.price()).thenReturn(productPrice);

        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));
        when(productMapper.toResponseDTO(savedProduct)).thenReturn(expectedResponse);

        // act;
        ProductResponseDTO actualResponse = productService.updateProduct(incomingRequestDTO, productId);


        // assert
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.id()).isEqualTo(productId);
        assertThat(actualResponse.name()).isEqualTo(productName);
        assertThat(actualResponse.price()).isEqualTo(productPrice);

        // verify
        verify(productRepository).save(any());
        verify(productRepository).findById(productId);
        verify(productMapper).toResponseDTO(savedProduct);




    }

    @Test
    void updateProduct_whenProductDoesNotExists_shouldBeThrowException()
    {
        Long productId = 2L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // act + assert
        assertThatThrownBy( () -> productService.updateProduct(any(ProductUpdateRequestDTO.class), eq(productId)))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("product already exist");

        // verify
        verify(productRepository).findById(productId);
        verify(productRepository,never()).save(any());
        verifyNoInteractions(productMapper);

    }







    // ================== updateProduct ===================
    @Test
    void deleteProduct_whenProductExist_shouldBeDeleted() {

        Long productId = 9L;
        Product expectedProduct = new Product();
        expectedProduct.setId(productId);
        expectedProduct.setName("deleted product");


        when(productRepository.findById(productId)).thenReturn(Optional.of(expectedProduct));

        // act
        productService.deleteProduct(productId);

        // verify
        verify(productRepository).findById(productId);
        verify(productRepository).delete(expectedProduct);
    }

    @Test
    void deleteProduct_whenProductDoesNotExist_shouldThrowException()
    {
        Long productId = 5L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // act
        assertThatThrownBy(() -> productService.deleteProduct(productId))
                .isExactlyInstanceOf(ProductNotFoundException.class)
                .hasMessage("product not found");

        verify(productRepository).findById(productId);
        verify(productRepository, never()).delete(any(Product.class));

    }


}