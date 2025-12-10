package com.e_commerce.E_Commerce.REST.API.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE) // TO ENABLE BATCHING
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

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;


    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL ,orphanRemoval = true )
    List<Order> orderList = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null)
            this.createdAt = LocalDateTime.now();
    }






}

