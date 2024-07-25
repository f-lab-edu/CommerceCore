package com.flab.CommerceCore.payment.domain.entity;

import com.flab.CommerceCore.common.enums.Status;
import com.flab.CommerceCore.order.domain.entity.Order;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne
    @JoinColumn(name="order_id")
    private Order order;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime paymentTime;
}
