package com.ecommerce.api.integration.repository;

import com.ecommerce.api.entity.Category;
import com.ecommerce.api.entity.Product;
import com.ecommerce.api.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ProductRepository")
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private Category persistCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return entityManager.persistAndFlush(category);
    }

    private Product buildProduct(String name, BigDecimal price, int stock, boolean active, Category category) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setStockQuantity(stock);
        product.setActive(active);
        product.setCategory(category);
        return product;
    }

    @Test
    @DisplayName("findAll(Pageable) returns a page respecting size and total count")
    void findAllRespectsPagination() {
        Category category = persistCategory("Electronics");
        for (int i = 1; i <= 15; i++) {
            entityManager.persist(buildProduct("Product " + i, BigDecimal.TEN, 10, true, category));
        }
        entityManager.flush();

        Page<Product> firstPage = productRepository.findAll(PageRequest.of(0, 10));

        assertThat(firstPage.getContent()).hasSize(10);
        assertThat(firstPage.getTotalElements()).isEqualTo(15);
        assertThat(firstPage.getTotalPages()).isEqualTo(2);
        assertThat(firstPage.hasNext()).isTrue();
    }

    @Test
    @DisplayName("findAll(Pageable) returns the remainder on the last page")
    void findAllReturnsRemainderOnLastPage() {
        Category category = persistCategory("Electronics");
        for (int i = 1; i <= 15; i++) {
            entityManager.persist(buildProduct("Product " + i, BigDecimal.TEN, 10, true, category));
        }
        entityManager.flush();

        Page<Product> secondPage = productRepository.findAll(PageRequest.of(1, 10));

        assertThat(secondPage.getContent()).hasSize(5);
        assertThat(secondPage.hasNext()).isFalse();
    }

    @Test
    @DisplayName("findAll(Pageable) returns an empty page when there is no data")
    void findAllReturnsEmptyPageWhenNoData() {
        Page<Product> page = productRepository.findAll(PageRequest.of(0, 10));

        assertThat(page.getContent()).isEmpty();
        assertThat(page.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("findById returns an empty Optional for a non-existent id")
    void findByIdReturnsEmptyWhenMissing() {
        var result = productRepository.findById(-1L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("persists and reloads a product with its category association intact")
    void persistsProductWithCategoryAssociation() {
        Category category = persistCategory("Books");
        Product saved = productRepository.saveAndFlush(
                buildProduct("Clean Code", new BigDecimal("45.99"), 100, true, category)
        );
        entityManager.clear();

        var reloaded = productRepository.findById(saved.getId());

        assertThat(reloaded).isPresent();
        assertThat(reloaded.get().getCategory().getId()).isEqualTo(category.getId());
        assertThat(reloaded.get().getPrice()).isEqualByComparingTo("45.99");
    }

    @Test
    @DisplayName("findAll(Specification, Pageable) filters using JpaSpecificationExecutor")
    void findAllWithSpecificationFilters() {
        Category category = persistCategory("Electronics");
        entityManager.persist(buildProduct("Laptop", new BigDecimal("999.00"), 5, true, category));
        entityManager.persist(buildProduct("Mouse", new BigDecimal("25.00"), 50, true, category));
        entityManager.flush();

        Specification<Product> nameContainsLaptop =
                (root, query, cb) -> cb.like(root.get("name"), "%Laptop%");

        Page<Product> result = productRepository.findAll(nameContainsLaptop, PageRequest.of(0, 10));

        assertThat(result.getContent()).extracting(Product::getName).containsExactly("Laptop");
    }

    @Test
    @DisplayName("findAll(Specification, Pageable) with a null specification behaves like findAll(Pageable)")
    void findAllWithNullSpecificationReturnsEverything() {
        Category category = persistCategory("Electronics");
        entityManager.persist(buildProduct("Laptop", new BigDecimal("999.00"), 5, true, category));
        entityManager.persist(buildProduct("Mouse", new BigDecimal("25.00"), 50, true, category));
        entityManager.flush();

        Page<Product> result = productRepository.findAll((Specification<Product>) null, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(2);
    }
}