package com.flab.CommerceCore.order.repository;

import com.flab.CommerceCore.common.annotation.LogRepositoryError;
import com.flab.CommerceCore.order.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

@LogRepositoryError
public interface OrderRepository extends JpaRepository<Order, Long> {

}
