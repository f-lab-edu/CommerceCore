package com.flab.CommerceCore.payment.domain.entity;

import com.flab.CommerceCore.order.domain.entity.Order;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne
    @JoinColumn(name="order_id")
    private Order order;

    private Double amount;
    private String status;
    private LocalDateTime paymentTime;
}
