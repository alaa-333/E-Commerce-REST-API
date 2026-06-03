package com.ecommerce.api.repository;

import com.ecommerce.api.entity.Category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    Page<Category> findAll(Pageable pageable);



    @Modifying
    @Query("UPDATE Category c SET c.active = false WHERE c.id = :id")
    int deleteCategoryById(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Category c SET c.name = :name, c.description = :description WHERE c.id = :id")
    int updateCategory(@Param("id") Long id, @Param("name") String name, @Param("description") String description);

    @Modifying
    @Query("update Category c set c.productCount = c.productCount + 1 where c.id = :id")
    int incrementProductCount(Long id);
}

