package com.flab.CommerceCore.product.domain.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
  private Long productId;
  private String productName;
  private String description;
  private BigDecimal price;
  private int quantity;
}
