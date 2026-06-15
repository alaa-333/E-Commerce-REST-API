package com.ecommerce.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Customer extends BaseEntity{

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String phone;

    @Embedded
    private Address address;

    @OneToOne(mappedBy = "customer_id", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;
}
