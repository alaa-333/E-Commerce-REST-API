package com.e_commerce.E_Commerce.REST.API.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "first_name" , nullable = false)
    private String firstName;
    @Column(name = "last_name" , nullable = false)
    private String lastName;
    @Column(unique = true , nullable = false)
    private String email;
    @Embedded // this annotation embedded value obj into entity
    private Address address;

    @Column(name = "phone_number",nullable = false)
    private String phone;
    private LocalDateTime createdAt;


    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL ,orphanRemoval = true )
    List<Order> orderList = new ArrayList<>();
    @Column(insertable = false)
    private long listSize = orderList.size();




}

