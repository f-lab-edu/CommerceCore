package com.flab.CommerceCore.payment.domain.entity;

import com.flab.CommerceCore.common.enums.Status;
import com.flab.CommerceCore.order.domain.entity.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime paymentTime;

    @Builder
    public Payment(BigDecimal amount) {
        this.amount = amount;
        this.paymentTime = LocalDateTime.now();
        this.status = Status.COMPLETED;
    }


    public void changeStatus(Status status) {
        this.status = status;
    }

}
