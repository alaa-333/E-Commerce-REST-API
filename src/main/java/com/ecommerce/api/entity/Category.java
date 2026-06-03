package com.ecommerce.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "categories")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Category extends BaseEntity{

    @Column(nullable = false, unique = true)
    private String name;
    private String description;
    @Builder.Default
    private boolean active =true;

    @Builder.Default
    private int productCount = 0;

    public boolean increaseProductCount() {

        productCount++;
        return true;
    }

}
