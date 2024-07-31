package com.flab.CommerceCore.user.repository;

import com.flab.CommerceCore.user.domain.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager em;

    public User findUser(Long userId){
        return em.find(User.class, userId);
    }

}
