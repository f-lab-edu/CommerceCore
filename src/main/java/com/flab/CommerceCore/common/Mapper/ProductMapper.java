package com.flab.CommerceCore.common.Mapper;

import com.flab.CommerceCore.product.domain.dto.ProductRequest;
import com.flab.CommerceCore.product.domain.dto.ProductResponse;
import com.flab.CommerceCore.product.domain.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

  public Product convertRequestToEntity(ProductRequest productRequest) {
    return Product.builder()
        .productName(productRequest.getProductName())
        .description(productRequest.getDescription())
        .price(productRequest.getPrice())
        .build();
  }

  public ProductResponse convertEntityToResponse(Product product,int quantity) {
    return ProductResponse.builder()
        .productId(product.getProductId())
        .productName(product.getProductName())
        .description(product.getDescription())
        .price(product.getPrice())
        .quantity(quantity)
        .build();
  }

}
