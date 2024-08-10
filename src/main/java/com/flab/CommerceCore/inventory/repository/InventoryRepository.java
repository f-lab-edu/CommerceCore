package com.flab.CommerceCore.inventory.repository;

import com.flab.CommerceCore.common.query.QueryConstant;
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
        return em.createQuery(QueryConstant.FIND_INVENTORY_BY_PRODUCT_ID, Inventory.class)
                .setParameter("productId", productId)
                .getSingleResult();
    }
}