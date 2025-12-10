package com.e_commerce.E_Commerce.REST.API.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "products")
public class Product
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
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
    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;
    private boolean active ; // instead of remove product from db when unavailable just mark is an active  = true -> mean available if false -> mean unavailable

    // relations
    @OneToMany(mappedBy = "product")
    private List<OrderItem> itemList = new ArrayList<>();





}
