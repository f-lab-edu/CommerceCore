package com.flab.CommerceCore.payment.repository;

import com.flab.CommerceCore.common.annotation.LogRepositoryError;
import com.flab.CommerceCore.payment.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

@LogRepositoryError
public interface PaymentRepository extends JpaRepository<Payment, Long> {
  Payment findByPaymentId(Long paymentId);
}
