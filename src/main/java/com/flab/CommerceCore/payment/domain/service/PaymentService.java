package com.flab.CommerceCore.payment.domain.service;

import com.flab.CommerceCore.common.enums.Status;
import com.flab.CommerceCore.order.domain.entity.Order;
import com.flab.CommerceCore.payment.domain.dto.PaymentRequest;
import com.flab.CommerceCore.payment.domain.entity.Payment;
import com.flab.CommerceCore.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository){
        this.paymentRepository = paymentRepository;
    }

    public Payment payment(PaymentRequest paymentRequest){

        Payment payment = Payment.createPayment(paymentRequest);

        if(callPaymentAPI(payment)){
            payment.changeStatus(Status.COMPLETED);
        }else{
            payment.changeStatus(Status.FAILED);
        }

        paymentRepository.save(payment);

        return paymentRepository.findById(payment.getPaymentId());

    }



    // 외부  payment API 호출
    public boolean callPaymentAPI(Payment payment){

        return true;
    }
}
