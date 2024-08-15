package com.flab.CommerceCore.payment.service;

import com.flab.CommerceCore.common.enums.Status;
import com.flab.CommerceCore.common.exceptions.BusinessException;
import com.flab.CommerceCore.common.exceptions.ErrorCode;
import com.flab.CommerceCore.payment.domain.entity.Payment;
import com.flab.CommerceCore.payment.repository.PaymentRepository;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository){
        this.paymentRepository = paymentRepository;
    }

    public Payment payment(BigDecimal totalAmount){

        Payment payment = Payment.createPayment(totalAmount);

        if(callPaymentAPI(payment)){
            payment.changeStatus(Status.COMPLETED);
        }else{
            payment.changeStatus(Status.FAILED);
            paymentRepository.save(payment);
            throw BusinessException.create(ErrorCode.PAYMENT_FAILED);
        }

        paymentRepository.save(payment);

        return paymentRepository.findById(payment.getPaymentId());

    }



    // 외부  payment API 호출
    public boolean callPaymentAPI(Payment payment){

        return true;
    }
}
