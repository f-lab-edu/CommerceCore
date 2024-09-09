package com.flab.CommerceCore.common.Mapper;

import com.flab.CommerceCore.common.exceptions.BusinessException;
import com.flab.CommerceCore.common.exceptions.ErrorCode;
import com.flab.CommerceCore.inventory.domain.dto.InventoryResponse;
import com.flab.CommerceCore.inventory.domain.entity.Inventory;
import com.flab.CommerceCore.product.domain.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InventoryMapper {

  public Inventory toEntity(Product product, int quantity) {
    return Inventory.builder()
        .product(product)
        .quantity(quantity)
        .build();
  }

  public InventoryResponse convertEntityToResponse(Inventory inventory) {
    return InventoryResponse.builder()
        .inventoryId(inventory.getInventoryId())
        .product(inventory.getProduct())
        .quantity(inventory.getQuantity())
        .lastUpdate(inventory.getLastUpdate())
        .build();
  }

}
