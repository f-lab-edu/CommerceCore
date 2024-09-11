package com.flab.CommerceCore.order.domain.entity;

import com.flab.CommerceCore.common.enums.Status;
import com.flab.CommerceCore.payment.domain.entity.Payment;
import com.flab.CommerceCore.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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


    @OneToMany(mappedBy = "order")
    private List<OrderProduct> orderProducts;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Builder
    public Order(User user, List<OrderProduct> orderProducts, Payment payment) {
        this.user = user;
        user.getOrders().add(this);
        this.orderProducts = orderProducts;
        this.payment = payment;
        this.status = payment != null ? payment.getStatus() : Status.PROCESSING;
        this.orderDate = LocalDateTime.now();
    }

    public void cancelOrder(Status status) {
        this.status = status;
    }
}
