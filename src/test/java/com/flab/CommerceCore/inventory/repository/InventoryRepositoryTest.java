package com.flab.CommerceCore.inventory.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.flab.CommerceCore.common.enums.InventoryOperation;
import com.flab.CommerceCore.inventory.domain.entity.Inventory;
import com.flab.CommerceCore.product.domain.entity.Product;
import com.flab.CommerceCore.product.repository.ProductRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;


@DataJpaTest
@ActiveProfiles("test")
class InventoryRepositoryTest {

  @Autowired
  private InventoryRepository inventoryRepository;

  @Autowired
  private ProductRepository productRepository;


  @Test
  @DisplayName("inventory 저장 후 조회")
  void saveAndFindInventoryById() {

    // given
   Product product = productRepository.save(Product.builder()
       .productName("Apple")
       .description("Red")
       .price(new BigDecimal(150)).
       build());

    Inventory inventory = Inventory.builder()
        .product(product)
        .quantity(10)
        .build();

    // when
    Inventory save = inventoryRepository.save(inventory);
    Inventory find = inventoryRepository.findByInventoryId(save.getInventoryId());

    // then
    assertEquals(save, find);
    assertEquals(find.getProduct().getProductId(),product.getProductId());
  }

  @Test
  @DisplayName("inventory 삭제 시 product 같이 삭제")
  void deleteInventoryById() {
    // given
    Product product = productRepository.save(Product.builder()
        .productName("Apple")
        .description("Red")
        .price(new BigDecimal(150)).
        build());

    Inventory inventory = inventoryRepository.save(Inventory.builder()
        .product(product)
        .quantity(10)
        .build());

    // when
    inventoryRepository.delete(inventory);

    // then
    assertNull(inventoryRepository.findByInventoryId(inventory.getInventoryId()));
    assertNull(productRepository.findByProductId(product.getProductId()));
  }

  @Test
  @DisplayName("재고 감소")
  void reduceQuantity(){
    // given
    Product product = productRepository.save(Product.builder()
        .productName("Apple")
        .description("Red")
        .price(new BigDecimal(150)).
        build());

    Inventory inventory = inventoryRepository.save(Inventory.builder()
        .product(product)
        .quantity(10)
        .build());

    int updateQuantity = 5;
    // when

    inventory.modifyQuantity(updateQuantity, InventoryOperation.DECREASE);

    inventoryRepository.flush();

    // then
    assertEquals(5,inventory.getQuantity());


  }

  @Test
  @DisplayName("재고 추가")
  void increaseQuantity() {
    Product product = productRepository.save(Product.builder()
        .productName("Apple")
        .description("Red")
        .price(new BigDecimal(150)).
        build());

    Inventory inventory = inventoryRepository.save(Inventory.builder()
        .product(product)
        .quantity(10)
        .build());

    int updateQuantity = 5;

    inventory.modifyQuantity(updateQuantity, InventoryOperation.INCREASE);

    inventoryRepository.flush();

    assertEquals(15, inventory.getQuantity());
  }


}