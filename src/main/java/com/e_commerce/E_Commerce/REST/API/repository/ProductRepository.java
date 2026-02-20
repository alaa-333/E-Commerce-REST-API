package com.e_commerce.E_Commerce.REST.API.repository;

import com.e_commerce.E_Commerce.REST.API.model.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product , Long> {


    boolean existsByName(String productName);
    Page<Product> findByCategory(String category , Pageable pageable);

    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice , Pageable pageable);

    Page<Product> findByNameStartingWithIgnoreCase(String name , Pageable pageable);


    @Modifying // tell jpa this is dml operation not select stat
    @Transactional // Required for DML operations (Update/Delete)
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - :quantity " +
            "WHERE p.id = :id AND p.stockQuantity >= :quantity")
    int reduceStock(@Param("id") Long id, @Param("quantity") int quantity);

}
