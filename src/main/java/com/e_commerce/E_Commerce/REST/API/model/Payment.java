package com.e_commerce.E_Commerce.REST.API.model;

import com.e_commerce.E_Commerce.REST.API.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
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
    @Column(name = "payment_method" , nullable = false)
    private String paymentMethod;
    @Column(nullable = false)
    private BigDecimal amount;

    @CreatedDate
    private LocalDateTime PaymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    private String TransactionId;
    private String paymentGatewayResponse; // represent response coming from gateway


    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;





}
