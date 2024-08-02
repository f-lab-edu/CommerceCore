package com.flab.CommerceCore.order.repository;

import com.flab.CommerceCore.order.domain.entity.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findById(Long orderId){
     return em.find(Order.class,orderId);
    }

}
