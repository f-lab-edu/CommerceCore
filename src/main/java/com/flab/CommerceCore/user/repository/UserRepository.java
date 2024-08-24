package com.flab.CommerceCore.user.repository;

import com.flab.CommerceCore.common.annotation.LogRepositoryError;
import com.flab.CommerceCore.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

@LogRepositoryError
public interface UserRepository extends JpaRepository<User, Long> {

  User findByUserId(Long userId);

}
