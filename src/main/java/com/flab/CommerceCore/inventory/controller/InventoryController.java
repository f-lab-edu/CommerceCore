package com.flab.CommerceCore.inventory.controller;

import com.flab.CommerceCore.inventory.domain.dto.InventoryResponse;
import com.flab.CommerceCore.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

  private final InventoryService inventoryService;

  @Autowired
  public InventoryController(InventoryService inventoryService) {
    this.inventoryService = inventoryService;
  }

  /**
   * 재고 수량을 증가시키는 API
   * @param inventoryId 재고 ID
   * @param quantity 증가시킬 수량
   * @return 업데이트된 재고 정보
   */
  @PatchMapping("/{inventoryId}/increase")
  public ResponseEntity<InventoryResponse> increaseInventory(@PathVariable("inventoryId") Long inventoryId,
      @RequestParam("quantity") int quantity) {
    InventoryResponse updatedInventory = inventoryService.increaseQuantity(inventoryId,quantity);
    return ResponseEntity.ok(updatedInventory);
  }

  /**
   * 재고 수량을 정확한 값으로 변경하는 API
   * @param inventoryId 재고 ID
   * @param quantity 설정할 새로운 수량
   * @return 업데이트된 재고 정보
   */
  @PutMapping("/{inventoryId}")
  public ResponseEntity<InventoryResponse> updateInventory(@PathVariable("inventoryId") Long inventoryId,
      @RequestParam("quantity") int quantity) {
    InventoryResponse updatedInventory = inventoryService.updateQuantity(inventoryId,quantity);
    return ResponseEntity.ok(updatedInventory);
  }

}
