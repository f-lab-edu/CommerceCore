package com.flab.CommerceCore.order.domain.entity;

import com.flab.CommerceCore.common.enums.Status;
import com.flab.CommerceCore.payment.domain.entity.Payment;
import com.flab.CommerceCore.user.domain.entity.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order")
    private List<OrderProduct> orderProducts;

    @OneToOne(mappedBy = "order")
    private Payment payment;
}
