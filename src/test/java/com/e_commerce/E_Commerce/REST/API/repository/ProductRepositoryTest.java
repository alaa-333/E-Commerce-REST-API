package com.e_commerce.E_Commerce.REST.API.repository;

import com.e_commerce.E_Commerce.REST.API.model.Product;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
@DataJpaTest
@DisplayName("Product Repository Test")
class ProductRepositoryTest {

    private final ProductRepository repository;
    private final TestEntityManager entityManager;

    private Product product1;
    private Product product2;
    private Product product3;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        // Initialize products directly here to ensure fresh state
        product1 = new Product();
        product1.setName("laptop");
        product1.setCategory("Electronics");
        product1.setPrice(new BigDecimal("999.99"));
        product1.setStockQuantity(10);

        product2 = new Product();
        product2.setName("Smartphone");
        product2.setCategory("Electronics");
        product2.setPrice(new BigDecimal("500.99"));
        product2.setStockQuantity(20);

        product3 = new Product();
        product3.setName("Desk Chair");
        product3.setCategory("Electronics");
        product3.setPrice(new BigDecimal("199.99"));
        product3.setStockQuantity(15);

        // save data
        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.persist(product3);
        entityManager.flush();
    }

    @Nested
    @DisplayName("Exists By Name Tests")
    class ExistsByName {
        @Test
        @DisplayName("Should return true when product name exists without case matching issues")
        void existsByName_whenProductExists_shouldReturnTrue() {
            boolean exists = repository.existsByName("laptop");
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Should return false when product name does not exist")
        void existsByName_whenProductDoesNotExists_shouldReturnFalse() {
            String productName = "sony headphone";
            boolean flag = repository.existsByName(productName);
            assertThat(flag).isFalse();
        }
    }

    @Nested
    @DisplayName("Find By Category Tests")
    class FindByCategory {
        @Test
        @DisplayName("Should return products when category exists")
        void findByCategory_whenCategoryExists_shouldReturnProducts() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> products = repository.findByCategory("Electronics", pageable);

            assertThat(products).isNotNull();
            assertThat(products.getContent()).hasSize(3); // All 3 are Electronics
            assertThat(products.getContent())
                    .extracting(Product::getCategory)
                    .containsOnly("Electronics");
        }

        @Test
        @DisplayName("Should return empty page when category does not exist")
        void findByCategory_whenCategoryDoesNotExists_shouldReturnEmptyPage() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> products = repository.findByCategory("games", pageable);
            assertThat(products).isEmpty();
        }
    }

    @Nested
    @DisplayName("Find By Price Tests")
    class FindByPrice {
        @Test
        @DisplayName("Should return products within price range")
        void findByPriceBetweenWhenInRange_ShouldReturnPage() {
            BigDecimal minPrice = new BigDecimal("100.00");
            BigDecimal maxPrice = new BigDecimal("700.00");
            Pageable pageable = PageRequest.of(0, 10);

            Page<Product> products = repository.findByPriceBetween(minPrice, maxPrice, pageable);

            assertThat(products).isNotNull();
            // Smartphone (500.99) and Desk Chair (199.99) are in range. Laptop (999.99) is
            // out.
            assertThat(products.getContent()).hasSize(2);
            assertThat(products.getContent())
                    .extracting(Product::getName)
                    .containsExactlyInAnyOrder("Smartphone", "Desk Chair");
        }

        @Test
        @DisplayName("Should return empty page when no products in price range")
        void findByPriceBetweenWhenPriceOutOfRange_ShouldReturnPage() {
            BigDecimal minPrice = new BigDecimal("1099.00");
            BigDecimal maxPrice = new BigDecimal("1199.00");
            Pageable pageable = PageRequest.of(0, 10);

            Page<Product> products = repository.findByPriceBetween(minPrice, maxPrice, pageable);
            assertThat(products).isEmpty();
        }
    }

    @Nested
    @DisplayName("Search By Name Tests")
    class SearchByName {
        @Test
        @DisplayName("Should return products starting with prefix (case insensitive)")
        void findByNameStartingWithIgnoreCase_whenPrefixExists_shouldReturnPage() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> products = repository.findByNameStartingWithIgnoreCase("desk", pageable);

            assertThat(products).isNotNull();
            assertThat(products.getContent()).hasSize(1);
            assertThat(products.getContent().get(0).getName()).isEqualTo("Desk Chair");
        }

        @Test
        @DisplayName("Should return empty page when prefix does not match")
        void findByNameStartingWithIgnoreCase_whenPrefixDoesNotExists_shouldReturnEmptyPage() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> products = repository.findByNameStartingWithIgnoreCase("tap", pageable);
            assertThat(products).isEmpty();
        }

        @Test
        @DisplayName("Should handle case insensitivity correctly")
        void findByNameStartingWithIgnore_whenCaseInsensitively_shouldReturnPage() {
            String prefixUppercase = "LAP";
            String prefixLowercase = "lap";
            Pageable pageable = PageRequest.of(0, 10);

            Page<Product> productsUppercase = repository.findByNameStartingWithIgnoreCase(prefixUppercase, pageable);
            Page<Product> productsLowercase = repository.findByNameStartingWithIgnoreCase(prefixLowercase, pageable);

            assertThat(productsUppercase.getContent()).isNotNull();
            assertThat(productsUppercase.getContent()).hasSize(1);
            assertThat(productsUppercase.getContent().get(0).getName()).isEqualTo("laptop");

            assertThat(productsLowercase.getContent()).isEqualTo(productsUppercase.getContent());
        }
    }

    @Nested
    @DisplayName("Stock Operations Tests")
    class StockOperations {
        @Test
        @DisplayName("Should reduce stock when sufficient quantity available")
        void reduceStock_whenSufficientStockAvailable_shouldReduceQuantity() {
            Long productId = product1.getId();
            Integer initialStock = product1.getStockQuantity();
            int requestedQuantity = 5;

            int effectedRows = repository.reduceStock(productId, requestedQuantity);

            // Update should return 1 (one row affected)
            assertThat(effectedRows).isEqualTo(1);

            entityManager.clear();
            Product updatedProduct = repository.findById(productId).orElseThrow();
            assertThat(updatedProduct.getStockQuantity()).isEqualTo(initialStock - requestedQuantity);
        }

        @Test
        @DisplayName("Should not reduce stock when insufficient quantity")
        void reduceStock_whenInsufficientStock_shouldDontReduceQuantity() {
            Long productId = product1.getId();
            Integer initialStock = product1.getStockQuantity();

            int effectedRows = repository.reduceStock(productId, 20); // 20 > 10

            assertThat(effectedRows).isZero();

            entityManager.clear();
            Product unchangedProduct = repository.findById(productId).orElseThrow();
            assertThat(unchangedProduct.getStockQuantity()).isEqualTo(initialStock);
        }

        @Test
        @DisplayName("Should not reduce stock when product not found")
        void reduceStock_whenProductNotFound_shouldDontReduceQuantity() {
            int effectedRows = repository.reduceStock(99L, 5);
            assertThat(effectedRows).isZero();
        }
    }

    @Nested
    @DisplayName("Find All By ID Tests")
    class FindAllById {
        @Test
        @DisplayName("Should return all requested products")
        void findAllById_whenProductsExists_shouldReturnProducts() {
            List<Long> productIds = List.of(
                    product1.getId(),
                    product2.getId(),
                    product3.getId());

            List<Product> products = repository.findAllById(productIds);

            assertThat(products).isNotNull();
            assertThat(products).hasSize(3);
            assertThat(products)
                    .extracting(Product::getName)
                    .containsExactlyInAnyOrder("laptop", "Smartphone", "Desk Chair");
        }
    }
}