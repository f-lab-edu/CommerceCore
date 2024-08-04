package com.flab.CommerceCore.order.domain.entity;

import com.flab.CommerceCore.common.enums.Status;
import com.flab.CommerceCore.payment.domain.entity.Payment;
import com.flab.CommerceCore.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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

    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order")
    private List<OrderProduct> orderProducts;

    @OneToOne(mappedBy = "order")
    private Payment payment;

    public static Order createOrder(User user,List<OrderProduct> orderProductList){
        Order order = new Order();
        order.changeUser(user);
        order.orderDate = LocalDateTime.now();
        for(OrderProduct orderProduct : orderProductList){
            order.addOrderProduct(orderProduct);
        }
        order.totalAmount = order.calculateTotalAmount();

        return order;
    }

    // 연관관계 메서드
    public void changeUser(User user){
        this.user = user;

    }

    public void addOrderProduct(OrderProduct orderProduct){
        orderProducts.add(orderProduct);
    }

    public void changePayment(Payment payment){
        this.payment = payment;
    }


    // 비즈니스 코드
    public void changeStatus(){
        if(this.payment.getStatus() == Status.COMPLETED){
            this.status = Status.COMPLETED;
        }else{
            this.status = Status.FAILED;
        }

    }

    public BigDecimal calculateTotalAmount(){

        for(OrderProduct orderProduct : orderProducts){
            totalAmount = totalAmount.add(orderProduct.getTotalPrice());
        }

        return totalAmount;
    }



}
