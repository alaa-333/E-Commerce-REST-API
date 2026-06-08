package com.ecommerce.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Where(clause = "active = true")
public class Product extends BaseEntity {

    @Column(nullable = false)
    private String name;
    @Column(length = 1000)
    private String description;
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    @Column(nullable = false)
    private Integer stockQuantity;

    private String imageUrl;

    private boolean active = true;

    @JoinColumn(name = "category_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;
}
