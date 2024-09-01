package com.flab.CommerceCore.payment.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.flab.CommerceCore.payment.domain.entity.Payment;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class PaymentRepositoryTest {

  @Autowired
  private PaymentRepository paymentRepository;

  private static final BigDecimal AMOUNT = new BigDecimal("10000");

  @Test
  void saveAndFindPaymentByPaymentId(){
    // given
    Payment payment = Payment.builder()
        .amount(AMOUNT)
        .build();

    // when
    Payment savePayment = paymentRepository.save(payment);
    Payment findPayment = paymentRepository.findByPaymentId(savePayment.getPaymentId());

    // then
    assertNotNull(findPayment);
    assertEquals(savePayment.getPaymentId(), findPayment.getPaymentId());
    assertEquals(savePayment.getAmount(), findPayment.getAmount());
    assertEquals(savePayment.getStatus(), findPayment.getStatus());
    assertEquals(savePayment.getPaymentTime(), findPayment.getPaymentTime());
  }



}