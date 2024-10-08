package com.flab.CommerceCore.product.repository;

import com.flab.CommerceCore.common.annotation.LogRepositoryError;
import com.flab.CommerceCore.product.domain.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

@LogRepositoryError
public interface ProductRepository extends JpaRepository<Product, Integer> {
  Product findByProductId(Long productId);
  Product findByProductName(String productName);

  List<Product> findAllByProductIdIn(List<Long> productIds);
}
