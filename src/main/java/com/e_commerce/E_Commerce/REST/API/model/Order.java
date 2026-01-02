package com.e_commerce.E_Commerce.REST.API.model;

import com.e_commerce.E_Commerce.REST.API.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
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
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_order_number" ,columnList = "order_number"),

        }
)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE) // TO ENABLE BATCHING
    private Long id;

    @CreatedDate
    @Column(name = "order_date")
    private LocalDateTime orderDate;
    @Column(name = "total_amount" , nullable = false)
    private BigDecimal totalAmount;
    @Column(name = "order_number")
    private String orderNumber;
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus = OrderStatus.PENDING;


    // Relations

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();
}
