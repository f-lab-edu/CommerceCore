package com.flab.CommerceCore.payment.service;

import com.flab.CommerceCore.common.enums.Status;
import com.flab.CommerceCore.common.exceptions.BusinessException;
import com.flab.CommerceCore.common.exceptions.ErrorCode;
import com.flab.CommerceCore.payment.domain.entity.Payment;
import com.flab.CommerceCore.payment.repository.PaymentRepository;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository){
        this.paymentRepository = paymentRepository;
    }

    /**
     * 결제를 처리하고 결제 객체를 반환
     * @param totalAmount 총 결제 금액
     * @return 결제 객체
     */
    public Payment payment(BigDecimal totalAmount){

        validateAmount(totalAmount);

        Payment payment = Payment.builder()
            .amount(totalAmount)
            .build();

        if (!callPaymentAPI(payment)) {
            processFailedPayment(payment);
        }

        return paymentRepository.save(payment);
    }

    /**
     * 총 결제 금액을 검증
     * @param totalAmount 총 결제 금액
     */
    private void validateAmount(BigDecimal totalAmount) {
        if (totalAmount == null) {
            log.error(ErrorCode.AMOUNT_NOT_FOUND.getDetail(), totalAmount);
            throw new BusinessException(ErrorCode.AMOUNT_NOT_FOUND);
        } else if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            log.error(ErrorCode.NEGATIVE_AMOUNT.getDetail(), totalAmount);
            throw new BusinessException(ErrorCode.NEGATIVE_AMOUNT);
        }
    }

    /**
     * 결제 실패를 처리
     * @param payment 결제 객체
     */
    private void processFailedPayment(Payment payment) {
        payment.changeStatus(Status.FAILED);
        paymentRepository.save(payment);
        log.error(ErrorCode.PAYMENT_FAILED.getDetail());
        throw BusinessException.create(ErrorCode.PAYMENT_FAILED);
    }


    /**
     * 외부 결제 API를 호출
     * @param payment 결제 객체
     * @return 결제 성공 여부
     */
    public boolean callPaymentAPI(Payment payment){
        return true;  // 실제 외부 API 호출이 필요할 경우 이 부분을 구현
    }
}
