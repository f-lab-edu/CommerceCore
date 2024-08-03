package com.flab.CommerceCore.order.domain.entity;

import com.flab.CommerceCore.common.enums.Status;
import com.flab.CommerceCore.payment.domain.entity.Payment;
import com.flab.CommerceCore.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@NoArgsConstructor
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

    public Order(User user, Payment payment, OrderProduct... orderProducts){
        setUser(user);
        this.orderDate = LocalDateTime.now();
        this.status = Status.PROCESSING;
        setPayment(payment);
        for(OrderProduct orderProduct : orderProducts){
            addOrderProduct(orderProduct);
        }
        this.totalAmount = calculateTotalAmount();
    }

    // 연관관계 메서드
    public void setUser(User user){
        this.user = user;
        user.getOrders().add(this);
    }

    public void addOrderProduct(OrderProduct orderProduct){
        orderProducts.add(orderProduct);
        orderProduct.setOrder(this);
    }

    public void setPayment(Payment payment){
        this.payment = payment;
        payment.setOrder(this);
    }


    // 비즈니스 코드
    public void changeStatus(Status status){
        this.status = status;
    }

    public BigDecimal calculateTotalAmount(){

        for(OrderProduct orderProduct : orderProducts){
            totalAmount = totalAmount.add(orderProduct.getTotalPrice());
        }

        return totalAmount;
    }



}
