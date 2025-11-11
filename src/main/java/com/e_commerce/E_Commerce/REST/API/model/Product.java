package com.e_commerce.E_Commerce.REST.API.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Product
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private BigDecimal price;
    @Column(name = "stock_quantity")
    private Integer stockQuantity;
    @Column(nullable = false)
    private String category;
    @Column(nullable = false)
    private String imgUrl;
    private boolean active; // instead of remove product from db when unavailable just mark is a active  = true -> mean available if false -> mean unavailable

    // relations
    @OneToMany(mappedBy = "product")
    private List<OrderItem> itemList = new ArrayList<>();





}
