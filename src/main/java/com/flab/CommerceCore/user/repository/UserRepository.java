package com.flab.CommerceCore.user.repository;

import com.flab.CommerceCore.common.annotation.LogRepositoryError;
import com.flab.CommerceCore.user.domain.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
@LogRepositoryError
public class UserRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(User user){
        em.persist(user);
    }

    public User findById(Long userId){
        return em.find(User.class, userId);
    }

}
