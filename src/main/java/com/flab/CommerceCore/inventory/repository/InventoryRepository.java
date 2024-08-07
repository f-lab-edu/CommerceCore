package com.flab.CommerceCore.inventory.repository;

import com.flab.CommerceCore.inventory.domain.entity.Inventory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class InventoryRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Inventory inventory){
        em.persist(inventory);
    }


    public Inventory findByProductId(Long productId){
        return em.createQuery("select i from Inventory i where i.product.productId = :productId", Inventory.class)
                .setParameter("productId", productId)
                .getSingleResult();
    }
}