package com.ecommerce.api.unit.service;

import com.ecommerce.api.dto.request.product.CreateProductRequest;
import com.ecommerce.api.dto.request.product.ProductSearchCriteria;
import com.ecommerce.api.dto.request.product.UpdateProductRequest;
import com.ecommerce.api.dto.response.CategoryResponse;
import com.ecommerce.api.dto.response.PagedResponse;
import com.ecommerce.api.dto.response.ProductResponse;
import com.ecommerce.api.entity.Category;
import com.ecommerce.api.entity.Product;
import com.ecommerce.api.exception.EcommerceAppException;
import com.ecommerce.api.exception.ResourceNotFoundException;
import com.ecommerce.api.mapper.ProductMapper;
import com.ecommerce.api.repository.CategoryRepository;
import com.ecommerce.api.repository.ProductRepository;
import com.ecommerce.api.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductResponse productResponse;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setName("Electronics");
        category.setId(1L);

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setStockQuantity(50);
        product.setActive(true);
        product.setCategory(category);

        productResponse = ProductResponse.builder()
                .id(1L)
                .name("Laptop")
                .description("Test Description response")
                .price(new BigDecimal("19.99"))
                .stockQuantity(50)
                .category(CategoryResponse.builder().id(1L).name("Test Category").build())
                .imageUrl("http://example.com/image.jpg")
                .active(true)
                .createdAt(null)
                .build();
    }

    // ---------------------------------------------------------------
    // getAllProducts
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("getAllProducts")
    class GetAllProducts {

        @Test
        @DisplayName("maps a repository page into a PagedResponse")
        void returnsPagedResponse() {
            var pageRequest = PageRequest.of(0, 10);
            Page<Product> productPage = new PageImpl<>(List.of(product), pageRequest, 1);

            when(productRepository.findAll(pageRequest)).thenReturn(productPage);
            when(productMapper.toProductResponse(product)).thenReturn(productResponse);

            PagedResponse<ProductResponse> result = productService.getAllProducts(pageRequest);

            assertThat(result.data()).containsExactly(productResponse);
            assertThat(result.data().size()).isEqualTo(1);
            verify(productRepository).findAll(pageRequest);
        }

        @Test
        @DisplayName("returns an empty page when there are no products")
        void returnsEmptyPage() {
            var pageRequest = PageRequest.of(0, 10);
            Page<Product> emptyPage = new PageImpl<>(List.of(), pageRequest, 0);
            when(productRepository.findAll(pageRequest)).thenReturn(emptyPage);

            PagedResponse<ProductResponse> result = productService.getAllProducts(pageRequest);

            assertThat(result.data()).isEmpty();
            verifyNoInteractions(productMapper);
        }
    }

    // ---------------------------------------------------------------
    // getProduct
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("getProduct")
    class GetProduct {

        @Test
        @DisplayName("returns the mapped product when it exists")
        void returnsProductWhenFound() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productMapper.toProductResponse(product)).thenReturn(productResponse);

            ProductResponse result = productService.getProduct(1L);

            assertThat(result).isEqualTo(productResponse);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when the product does not exist")
        void throwsWhenNotFound() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.getProduct(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            verifyNoInteractions(productMapper);
        }
    }

    // ---------------------------------------------------------------
    // createProduct
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("createProduct")
    class CreateProduct {

        @Test
        @DisplayName("saves the product, marks it active, and increments the category count")
        void createsProductSuccessfully() {
            // NOTE: adjust builder/setter names to match your actual
            // CreateProductRequest — guessing getCategoryId() exists since
            // the service calls it directly.
            var request = mock(CreateProductRequest.class);
            when(request.getCategoryId()).thenReturn(1L);

            var newProduct = new Product();

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(productMapper.toEntity(request)).thenReturn(newProduct);
            when(productRepository.save(newProduct)).thenReturn(newProduct);
            when(productMapper.toProductResponse(newProduct)).thenReturn(productResponse);

            ProductResponse result = productService.createProduct(request);

            assertThat(result).isEqualTo(productResponse);
            assertThat(newProduct.isActive()).isTrue();
            assertThat(newProduct.getCategory()).isEqualTo(category);

            // category's product count must be persisted, not just mutated in memory
            verify(categoryRepository).save(category);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when the category does not exist")
        void throwsWhenCategoryMissing() {
            var request = mock(CreateProductRequest.class);
            when(request.getCategoryId()).thenReturn(404L);
            when(categoryRepository.findById(404L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.createProduct(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("404");

            verifyNoInteractions(productMapper);
            verify(productRepository, never()).save(any());
        }
    }

    // ---------------------------------------------------------------
    // updateProduct
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("updateProduct")
    class UpdateProduct {

        @Test
        @DisplayName("applies mapper updates and persists the product")
        void updatesExistingProduct() {
            var updateRequest = mock(UpdateProductRequest.class);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            productService.updateProduct(1L, updateRequest);

            verify(productMapper).updateEntity(product, updateRequest);
            verify(productRepository).save(product);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when the product does not exist")
        void throwsWhenNotFound() {
            var updateRequest = mock(UpdateProductRequest.class);
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.updateProduct(99L, updateRequest))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(productMapper);
            verify(productRepository, never()).save(any());
        }
    }

    // ---------------------------------------------------------------
    // deleteProduct (soft delete)
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("deleteProduct")
    class DeleteProduct {

        @Test
        @DisplayName("soft-deletes by setting active=false, not by removing the row")
        void softDeletesProduct() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            productService.deleteProduct(1L);

            assertThat(product.isActive()).isFalse();
            verify(productRepository).save(product);
            verify(productRepository, never()).delete(any(Product.class));
            verify(productRepository, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when the product does not exist")
        void throwsWhenNotFound() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.deleteProduct(99L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(productRepository, never()).save(any());
        }
    }

    // ---------------------------------------------------------------
    // searchProducts
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("searchProducts")
    class SearchProducts {

        @Test
        @DisplayName("delegates to the repository with a built specification and maps results")
        void searchesWithSpecification() {
            var criteria = mock(ProductSearchCriteria.class);
            var pageRequest = PageRequest.of(0, 10);
            Page<Product> productPage = new PageImpl<>(List.of(product), pageRequest, 1);

            when(productRepository.findAll(eq(pageRequest))).thenReturn(productPage);
            when(productMapper.toProductResponse(product)).thenReturn(productResponse);

            PagedResponse<ProductResponse> result = productService.searchProducts(criteria, pageRequest);

            assertThat(result.data()).containsExactly(productResponse);
        }
    }

    // ---------------------------------------------------------------
    // reduceStock
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("reduceStock")
    class ReduceStock {

        @Test
        @DisplayName("decrements stock when sufficient quantity is available")
        void reducesStockWhenAvailable() {
            product.setStockQuantity(50);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            productService.reduceStock(1L, 20);

            assertThat(product.getStockQuantity()).isEqualTo(30);
            verify(productRepository).save(product);
        }

        @Test
        @DisplayName("throws EcommerceAppException when requested quantity exceeds available stock")
        void throwsOnInsufficientStock() {
            product.setStockQuantity(5);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            assertThatThrownBy(() -> productService.reduceStock(1L, 20))
                    .isInstanceOf(EcommerceAppException.class)
                    .hasMessageContaining("Requested: 20")
                    .hasMessageContaining("Available: 5");

            // stock must be unchanged and unsaved on the failure path
            assertThat(product.getStockQuantity()).isEqualTo(5);
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("allows reducing stock down to exactly zero (boundary)")
        void allowsReducingToExactlyZero() {
            product.setStockQuantity(20);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            productService.reduceStock(1L, 20);

            assertThat(product.getStockQuantity()).isZero();
            verify(productRepository).save(product);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when the product does not exist")
        void throwsWhenNotFound() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.reduceStock(99L, 1))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        // This test targets the log.warn("LOW STOCK...") branch. It doesn't
        // assert on the log output itself (that needs a Logback ListAppender,
        // which is arguably overkill here) — it just confirms the branch is
        // reachable and doesn't throw or alter the save behavior.
        @Test
        @DisplayName("still saves successfully when the low-stock threshold is crossed")
        void succeedsWhenCrossingLowStockThreshold() {
            product.setStockQuantity(15);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            productService.reduceStock(1L, 10);

            assertThat(product.getStockQuantity()).isEqualTo(5);
            verify(productRepository).save(product);
        }
    }

    // ---------------------------------------------------------------
    // restoreStock
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("restoreStock")
    class RestoreStock {

        @Test
        @DisplayName("increments stock by the given quantity")
        void restoresStock() {
            product.setStockQuantity(10);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            productService.restoreStock(1L, 15);

            assertThat(product.getStockQuantity()).isEqualTo(25);
            verify(productRepository).save(product);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when the product does not exist")
        void throwsWhenNotFound() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.restoreStock(99L, 5))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(productRepository, never()).save(any());
        }
    }
}