package com.flab.CommerceCore.product.repository;

import com.flab.CommerceCore.product.domain.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ProductRepository {

    @PersistenceContext
    EntityManager em;

    public void save(Product product){
        if(product.getProductId() == null){
            em.persist(product);
        }else{
            em.merge(product);
        }
    }
    public Product findById(Long productId){
        return em.find(Product.class,productId);
    }

    public List<Product> findAll(){
        return em.createQuery("select p from Product p",Product.class)
                .getResultList();
    }

}
