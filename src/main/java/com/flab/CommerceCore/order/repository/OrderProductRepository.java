package com.flab.CommerceCore.order.repository;

import com.flab.CommerceCore.order.domain.entity.OrderProduct;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OrderProductRepository {

    @Autowired
    private EntityManager em;

    public void save(OrderProduct orderProduct){
        em.persist(orderProduct);
    }
}
