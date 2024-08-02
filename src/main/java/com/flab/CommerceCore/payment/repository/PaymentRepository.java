package com.flab.CommerceCore.payment.repository;

import com.flab.CommerceCore.payment.domain.entity.Payment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepository {

    @PersistenceContext
    EntityManager em;

    public void save(Payment payment){
        em.persist(payment);
    }

    public Payment findById(Long paymentId){
        return em.find(Payment.class,paymentId);
    }


}
