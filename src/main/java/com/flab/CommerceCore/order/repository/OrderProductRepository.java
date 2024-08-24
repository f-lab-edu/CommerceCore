package com.flab.CommerceCore.order.repository;


import com.flab.CommerceCore.common.annotation.LogRepositoryError;
import com.flab.CommerceCore.order.domain.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

@LogRepositoryError
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

}
