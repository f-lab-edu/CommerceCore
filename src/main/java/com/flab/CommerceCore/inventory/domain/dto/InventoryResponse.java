package com.flab.CommerceCore.inventory.domain.dto;

import com.flab.CommerceCore.product.domain.entity.Product;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor // 모든 필드를 받는 생성자 추가
public class InventoryResponse {

  private Long inventoryId;
  private Product product;
  private int quantity;
  private LocalDateTime lastUpdate;

}
