package com.e_commerce.E_Commerce.REST.API.model;

import com.e_commerce.E_Commerce.REST.API.payment.PaymentMethod;
import com.e_commerce.E_Commerce.REST.API.payment.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "payments")
public class Payment
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;


    @Column(nullable = false)
    private BigDecimal amount;

    @CreatedDate
    private LocalDateTime PaymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;
    private String paymentGatewayResponse; // represent response coming from gateway


    @OneToOne
    @JoinColumn(name = "order_id" , unique = true)
    private Order order;





}
