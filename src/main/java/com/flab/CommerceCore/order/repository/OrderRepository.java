package com.flab.CommerceCore.order.repository;

import com.flab.CommerceCore.common.annotation.LogRepositoryError;
import com.flab.CommerceCore.order.domain.entity.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

@LogRepositoryError
public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findAllByUserEmail(String userEmail);
  Order findByOrderId(Long orderId);
}
