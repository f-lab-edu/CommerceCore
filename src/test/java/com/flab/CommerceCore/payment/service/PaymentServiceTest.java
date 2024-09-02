package com.flab.CommerceCore.payment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flab.CommerceCore.common.enums.Status;
import com.flab.CommerceCore.common.exceptions.BusinessException;
import com.flab.CommerceCore.payment.domain.entity.Payment;
import com.flab.CommerceCore.payment.repository.PaymentRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

  @Mock
  PaymentService service;

  @Mock
  PaymentRepository paymentRepository;

  @Spy
  @InjectMocks
  PaymentService paymentService;

  /**
   * 금액이 null 인 경우 예외가 발생하는지 테스트
   */
  @Test
  void createPayment_Fail_Amount_null() {
    // 금액이 null 일 때 BusinessException 이 발생하는지 검증합
    assertThrows(BusinessException.class, () -> {
      paymentService.payment(null);
    });
  }


  /**
   * 금액이 음수인 경우 예외가 발생하는지 테스트
   */
  @Test
  void createPayment_Fail_Amount_Negative(){
    BigDecimal amount = new BigDecimal(-100);
    // 금액이 음수일 때 BusinessException 이 발생하는지 검증
    assertThrows(BusinessException.class, () -> {
      paymentService.payment(amount);
    });
  }

  /**
   * 외부 결제 API 호출이 실패했을 때 Payment 가 FAILED 상태로 저장되고, 예외가 발생하는지 테스트
   */
  @Test
  void createPayment_Fail_PG_API_Error(){
    // given : 유효한 금액이 주어졌을 때
    BigDecimal amount = new BigDecimal(100);

    // callPaymentAPI 메서드가 false 를 반환하도록 Mock 설정
    doReturn(false).when(paymentService).callPaymentAPI(any(Payment.class));

    // 결제가 실패할 때 BusinessException 이 발생하는지 검증
    assertThrows(BusinessException.class, () -> {
      paymentService.payment(amount);
    });

    // save() 메서드에 전달된 Payment 객체를 캡처
    ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
    verify(paymentRepository).save(paymentCaptor.capture());

    // 캡처된 Payment 객체의 상태가 FAILED 인지 확인
    Payment capturedPayment = paymentCaptor.getValue();
    assertEquals(Status.FAILED, capturedPayment.getStatus());

  }

  @Test
  void createPayment_Fail_Repository_Error(){
    BigDecimal amount = new BigDecimal(100);

    when(paymentRepository.save(any(Payment.class))).thenThrow(new DataAccessException("DB Error") {});

    assertThrows(DataAccessException.class, () -> {
      paymentService.payment(amount);
    });

    verify(paymentRepository, times(1)).save(any(Payment.class));
  }

  /**
   * 결제가 성공적으로 처리되고, Payment 객체가 제대로 저장되는지 테스트
   */
  @Test
  void createPayment_Success(){
    // given : 유효한 금액과 Payment 객체가 주어졌을 때
    BigDecimal amount = new BigDecimal(100);

    Payment payment = Payment.builder()
        .amount(amount)
        .build();

    // save 메서드가 payment 객체를 반환하도록 Mock 설정
    when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

    // 결제가 성공적으로 처리되었을 때
    Payment savePayment = paymentService.payment(amount);

    // 저장된 Payment 객체가 null 이 아니고, 예상된 값을 가지는지 검증
    assertNotNull(savePayment);
    assertEquals(amount, savePayment.getAmount());
    assertEquals(payment.getPaymentTime(), savePayment.getPaymentTime());
    assertEquals(payment.getStatus(), savePayment.getStatus());

    // save() 메서드에 전달된 Payment 객체를 캡처 캡처
    ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
    verify(paymentRepository).save(paymentCaptor.capture());

    // 캡처된 Payment 객체의 상태가 COMPLETED 인지 확인
    Payment capturedPayment = paymentCaptor.getValue();
    assertEquals(Status.COMPLETED, capturedPayment.getStatus());
  }



}