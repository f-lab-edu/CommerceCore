package com.flab.CommerceCore.product.repository;

import com.flab.CommerceCore.product.domain.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository {

    @PersistenceContext
    EntityManager em;

    public void save(Product product){
        em.persist(product);
    }

}
