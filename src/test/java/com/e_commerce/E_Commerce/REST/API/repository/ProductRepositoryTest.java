package com.e_commerce.E_Commerce.REST.API.repository;

import com.e_commerce.E_Commerce.REST.API.model.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@DataJpaTest
class ProductRepositoryTest {

    private final ProductRepository repository;
    private final TestEntityManager entityManager;

    private Product product1;
    private Product product2;
    private Product product3;

    @BeforeEach
    void setUp()
    {
        repository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        // create test product;

        // product 1
        product1.setName("laptop");
        product1.setCategory("Electronics");
        product1.setPrice(new BigDecimal("999.99"));
        product1.setStockQuantity(10);

        // product 2
        product2.setName("Smartphone");
        product2.setCategory("Electronics");
        product2.setPrice(new BigDecimal("500.99"));
        product2.setStockQuantity(20);

        // product 3
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



    // ================ existsByName ==============
    @Test
    void existsByName_whenProductExists_shouldReturnTrue() {
        // act
        boolean exists = repository.existsByName("Laptop");

        // assert
        assertThat(exists).isTrue();
    }

    @Test
    void existsByName_whenProductDoesNotExists_shouldReturnFalse()
    {
        // arrange
        String productName = "sony headphone";

        boolean flag = repository.existsByName(productName);

        assertThat(flag).isFalse();

    }




    // ================ findByCategory ==============
    @Test
    void findByCategory_whenCategoryExists_shouldReturnProducts() {

        Pageable pageable = PageRequest.of(0 , 10);

        Page<Product> products = repository.findByCategory("Electronics", pageable);

        // assert
        assertThat(products).isNotNull();
        assertThat(products.getContent()).hasSize(2);
        assertThat(products.getContent())
                .extracting(Product::getCategory)
                .containsOnly("Electronics");

    }

    @Test
    void findByCategory_whenCategoryDoesNotExists_shouldReturnEmptyPage() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> products = repository.findByCategory("games", pageable);

        // assert
        assertThat(products).isEmpty();

    }





    // ================ findByPriceBetween ==============
    @Test
    void findByPriceBetweenWhenInRange_ShouldReturnPage() {

        BigDecimal minPrice = new BigDecimal("100.00");
        BigDecimal maxPrice = new BigDecimal("700.00");
        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> products = repository.findByPriceBetween(minPrice, maxPrice, pageable);

        assertThat(products).isNotNull();
        assertThat(products.getContent()).hasSize(2);
        assertThat(products.getContent())

                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Smartphone", "Desk Chair");



    }

    @Test
    void findByPriceBetweenWhenPriceOutOfRange_ShouldReturnPage() {

        BigDecimal minPrice = new BigDecimal("1099.00");
        BigDecimal maxPrice = new BigDecimal("1199.00");
        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> products = repository.findByPriceBetween(minPrice, maxPrice, pageable);

        assertThat(products).isEmpty();

    }





    // ================ findByNameStartingWithIgnoreCase ==============

    @Test
    void findByNameStartingWithIgnoreCase_whenPrefixExists_shouldReturnPage() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> products = repository.findByNameStartingWithIgnoreCase("desk", pageable);

        // assert
        assertThat(products).isNotNull();
        assertThat(products.getContent()).hasSize(1);
        assertThat(products.getContent().get(0).getName()).isEqualTo("Desk Chair");


    }

    @Test
    void findByNameStartingWithIgnoreCase_whenPrefixDoesNotExists_shouldReturnEmptyPage () {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> products = repository.findByNameStartingWithIgnoreCase("tap", pageable);

        // assert
        assertThat(products).isEmpty();

    }

    @Test
    void findByNameStartingWithIgnore_whenCaseInsensitively_shouldReturnPage() {
        String prefixUppercase = "LAP";
        String prefixLowercase = "lap";

        Pageable pageable = PageRequest.of(0, 10);

        // act
        Page<Product> productsUppercase = repository.findByNameStartingWithIgnoreCase(prefixUppercase, pageable);
        Page<Product> productsLowercase = repository.findByNameStartingWithIgnoreCase(prefixLowercase, pageable);


        // assert

        assertThat(productsUppercase.getContent()).isNotNull();
        assertThat(productsUppercase.getContent()).hasSize(1);

        assertThat(productsLowercase.getContent()).isEqualTo(productsUppercase.getContent());

        assertThat(productsLowercase.getContent()).isNotNull();
        assertThat(productsLowercase.getContent()).hasSize(1);

    }





    // ================ reduceStock ==============
    @Test
    void reduceStock_whenSufficientStockAvailable_shouldReduceQuantity() {

        Long productId = product1.getId();
        Integer initialStock = product1.getStockQuantity();

        int effectedRows = repository.reduceStock(productId, 5);
        int requestedQuantity = 5;

        assertThat(effectedRows).isEqualTo(requestedQuantity);

        entityManager.clear();
        Product updatedProduct = repository.findById(productId).orElseThrow();
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(initialStock-requestedQuantity);

    }
    @Test
    void reduceStock_whenInsufficientStock_shouldDontReduceQuantity() {

        Long productId = product1.getId();
        Integer initialStock = product1.getStockQuantity();


        int effectedRows = repository.reduceStock(productId, 20);


        assertThat(effectedRows).isZero();

        entityManager.clear();
        Product unchangedProduct = repository.findById(productId).orElseThrow();
        assertThat(unchangedProduct.getStockQuantity()).isEqualTo(initialStock);

    }

    @Test
    void reduceStock_whenProductNotFound_shouldDontReduceQuantity() {
        int effectedRows = repository.reduceStock(99L, 5);

        assertThat(effectedRows).isZero();

    }





    @Test
    void findAllById_whenProductsExists_shouldReturnProducts() {

        List<Long> productIds =  List.of(
                product1.getId(),
                product2.getId(),
                product3.getId()
        );

        List<Product> products = repository.findAllById(productIds);

        assertThat(products).isNotNull();
        assertThat(products).hasSize(30);
        assertThat(products)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("laptop", "Smartphone", "Desk Chair");


    }
}