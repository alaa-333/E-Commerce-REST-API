package com.e_commerce.E_Commerce.REST.API.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
// Explicitly exclude the customer field
@ToString(exclude = "user")
@EqualsAndHashCode(exclude = "user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customer_created_at", columnList = "created_at")
})
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "first_name" , nullable = false)
    private String firstName;
    @Column(name = "last_name" , nullable = false)
    private String lastName;
    @Embedded // this annotation embedded value obj into entity
    private Address address;

    @Column(name = "phone_number",nullable = false)
    private String phone;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;


    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL ,orphanRemoval = true )
    private List<Order> orderList = new ArrayList<>();

    @OneToOne
    @JsonBackReference

    @JoinColumn(name = "user_id")
    private User user;


    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null)
            this.createdAt = LocalDateTime.now();
    }


    public void addOrder(Order order)
    {
        orderList.add(order);
        order.setCustomer(this);
    }




}

