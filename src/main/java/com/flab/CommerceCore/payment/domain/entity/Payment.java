package com.flab.CommerceCore.payment.domain.entity;

import com.flab.CommerceCore.common.enums.Status;
import com.flab.CommerceCore.order.domain.entity.Order;
import com.flab.CommerceCore.payment.domain.dto.PaymentRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public static Payment createPayment(PaymentRequest paymentRequest){
        Payment payment = new Payment();
        payment.paymentTime = LocalDateTime.now();
        payment.amount = paymentRequest.getAmount();

        return payment;
    }

    public void changeStatus(Status status) {
        this.status = status;
    }

}
