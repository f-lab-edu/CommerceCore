package com.flab.CommerceCore.inventory.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flab.CommerceCore.common.Mapper.InventoryMapper;
import com.flab.CommerceCore.common.exceptions.BusinessException;
import com.flab.CommerceCore.common.exceptions.ErrorCode;
import com.flab.CommerceCore.inventory.domain.dto.InventoryResponse;
import com.flab.CommerceCore.inventory.domain.entity.Inventory;
import com.flab.CommerceCore.inventory.repository.InventoryRepository;
import com.flab.CommerceCore.product.domain.entity.Product;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

  @InjectMocks
  InventoryService inventoryService;

  @Mock
  InventoryRepository inventoryRepository;

  @Mock
  InventoryMapper mapper;

  @Test
  @DisplayName("정상적인 재고 생성 테스트")
  void createInventorySuccess() {
    // given
    Product product = createTestProduct("Test Product");
    Inventory inventory = createTestInventory(product, 10);
    InventoryResponse response = InventoryResponse.builder()
        .inventoryId(1L)
        .product(product)
        .quantity(10)
        .build();

    when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
    when(mapper.toEntity(product,10)).thenReturn(inventory);
    when(mapper.convertEntityToResponse(inventory)).thenReturn(response);

    // when
    InventoryResponse createdInventory = inventoryService.createInventory(product, 10);

    // then
    assertNotNull(createdInventory);
    assertEquals(10, createdInventory.getQuantity());
    assertEquals("Test Product", createdInventory.getProduct().getProductName());

    verify(inventoryRepository, times(1)).save(any(Inventory.class));

  }

  @Test
  @DisplayName("중복된 상품 등록 시도시 예외 발생 테스트")
  void createInventoryFailDuplicateProduct(){
    // given
    Product product = createTestProduct("Test Product");
    Inventory inventory = createTestInventory(product, 10);


    when(inventoryRepository.findByProductId(product.getProductId())).thenReturn(inventory);

    // when
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      inventoryService.createInventory(product, 2);
    });

    // then
    assertEquals(ErrorCode.DUPLICATED_PRODUCT, exception.getErrorCode());
  }

  @Test
  @DisplayName("정상적인 재고 삭제 테스트")
  void deleteInventorySuccess() {
    // given
    Inventory inventory = createTestInventory(createTestProduct("Test Product"), 10);
    when(inventoryRepository.findByInventoryId(1L)).thenReturn(inventory);

    // when
    inventoryService.deleteInventory(1L);

    // then
    verify(inventoryRepository, times(1)).deleteById(1L);

  }

  @Test
  @DisplayName("존재하지 않는 재고 삭제 시 예외 발생 테스트")
  void deleteInventoryFailNotFound(){
    // given
    when(inventoryRepository.findByInventoryId(1L)).thenReturn(null);

    // when
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      inventoryService.deleteInventory(1L);
    });

    // then
    assertEquals(ErrorCode.INVENTORY_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("모든 재고 조회 성공 테스트")
  void findAllInventorySuccess() {
    // given
    Product product1 = createTestProduct("Product 1");
    Product product2 = createTestProduct("Product 2");
    Inventory inventory1 = createTestInventory(product1, 10);
    Inventory inventory2 = createTestInventory(product2, 15);

    when(inventoryRepository.findAll()).thenReturn(Arrays.asList(inventory1, inventory2));

    // when
    List<Inventory> inventories = inventoryService.findAllInventory();

    // then
    assertEquals(2, inventories.size());
    assertEquals("Product 1", inventories.get(0).getProduct().getProductName());
    assertEquals("Product 2", inventories.get(1).getProduct().getProductName());
    verify(inventoryRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("재고 수량 감소 성공 테스트")
  void reduceQuantitySuccess() {
    // given
    InventoryResponse response = InventoryResponse.builder()
        .inventoryId(1L)
        .quantity(5)
        .build();

    Inventory inventory = createTestInventory(createTestProduct("Test Product"), 10);
    when(inventoryRepository.findByProductId(1L)).thenReturn(inventory);
    when(mapper.convertEntityToResponse(inventory)).thenReturn(response);


    // when
    InventoryResponse inventoryResponse = inventoryService.reduceQuantity(1L,5);

    // then
    assertEquals(5, inventoryResponse.getQuantity());
    verify(inventoryRepository, times(1)).findByProductId(1L);
  }

  @Test
  @DisplayName("존재하지 않는 재고 수량 감소 시 예외 발생 테스트")
  void reduceQuantityFailDueToNotFound() {
    // given
    when(inventoryRepository.findByProductId(1L)).thenReturn(null);

    // when/then
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      inventoryService.reduceQuantity(1L,10);
    });

    assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
    verify(inventoryRepository, times(1)).findByProductId(1L);
  }

  @Test
  @DisplayName("재고 수량 증가 성공 테스트")
  void increaseQuantitySuccess() {
    // given
    Inventory inventory = createTestInventory(createTestProduct("Test Product"), 10);
    when(inventoryRepository.findByInventoryId(1L)).thenReturn(inventory);

    // when
    inventoryService.increaseQuantity(1L,5);

    // then
    assertEquals(15, inventory.getQuantity());
    verify(inventoryRepository, times(1)).findByInventoryId(1L);
  }

  @Test
  @DisplayName("존재하지 않는 재고 수량 증가 시 예외 발생 테스트")
  void increaseQuantityFailDueToNotFound() {
    // given
    when(inventoryRepository.findByInventoryId(1L)).thenReturn(null);


    // when/then
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      inventoryService.increaseQuantity(1L,5);
    });

    assertEquals(ErrorCode.INVENTORY_NOT_FOUND, exception.getErrorCode());
    verify(inventoryRepository, times(1)).findByInventoryId(1L);
  }

  @Test
  @DisplayName("재고 수량 업데이트 성공 테스트")
  void updateQuantitySuccess() {
    Product product = createTestProduct("Test Product");
    Inventory inventory = createTestInventory(product, 1);

    InventoryResponse expectedResponse = InventoryResponse.builder()
        .inventoryId(1L)
        .quantity(10)  // 업데이트 후 수량
        .product(product)
        .build();


    when(inventoryRepository.findByInventoryId(1L)).thenReturn(inventory);
    when(mapper.convertEntityToResponse(inventory)).thenReturn(expectedResponse);

    // when
    InventoryResponse actualResponse = inventoryService.updateQuantity(1L,10);

    // then
    assertEquals(10, actualResponse.getQuantity());
    assertEquals(expectedResponse.getInventoryId(), actualResponse.getInventoryId());
    assertEquals(expectedResponse.getProduct(), actualResponse.getProduct());
  }

  @Test
  @DisplayName("존재하지 않는 재고 수량 업데이트 시 예외 발생 테스트")
  void updateQuantityFailDueToNotFound() {
    // given
    when(inventoryRepository.findByInventoryId(1L)).thenReturn(null);


    // when/then
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      inventoryService.updateQuantity(1L,10);
    });

    assertEquals(ErrorCode.INVENTORY_NOT_FOUND, exception.getErrorCode());
    verify(inventoryRepository, times(1)).findByInventoryId(1L);
  }

  @Test
  @DisplayName("재고 생성 중 DB 오류 발생 시 예외 테스트")
  void createInventoryThrowsRepositoryError() {
    // given
    Product product = createTestProduct("Test Product");
    Inventory inventory = createTestInventory(product, 10);

    when(inventoryRepository.save(any(Inventory.class))).thenThrow(new DataAccessException("DB ERROR") {});
    when(mapper.toEntity(product,10)).thenReturn(inventory);

    // when/then
    DataAccessException exception = assertThrows(DataAccessException.class, () -> {
      inventoryService.createInventory(product, 10);
    });

    assertEquals("DB ERROR", exception.getMessage());
  }

  @Test
  @DisplayName("재고 조회 중 DB 오류 발생 시 예외 테스트")
  void findAllInventoryThrowsRepositoryError() {
    // given
    when(inventoryRepository.findAll()).thenThrow(new DataAccessException("DB ERROR") {});

    // when/then
    DataAccessException exception = assertThrows(DataAccessException.class, () -> {
          inventoryService.findAllInventory();
    });

    assertEquals("DB ERROR", exception.getMessage());

  }

  @Test
  @DisplayName("재고 삭제 중 DB 오류 발생 시 예외 테스트")
  void deleteInventoryThrowsRepositoryError() {
    // given
    Inventory inventory = createTestInventory(createTestProduct("Test Product"), 10);
    when(inventoryRepository.findByInventoryId(1L)).thenReturn(inventory);
    doThrow(new DataAccessException("DB ERROR") {}).when(inventoryRepository).deleteById(1L);

    // when/then
    DataAccessException exception = assertThrows(DataAccessException.class, () -> {
      inventoryService.deleteInventory(1L);
    });

    assertEquals("DB ERROR", exception.getMessage());
  }

  @Test
  @DisplayName("재고 업데이트 중 수량이 음수일 경우 예외 테스트")
  void updateInventoryNegativeQuantity(){
    // given
    Inventory inventory = createTestInventory(createTestProduct("Test Product"), 10);
    when(inventoryRepository.findByInventoryId(1L)).thenReturn(inventory);

    // when
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      inventoryService.updateQuantity(1L, -10);
    });

    // then
    assertEquals(ErrorCode.NEGATIVE_QUANTITY,exception.getErrorCode());

  }

  @Test
  @DisplayName("재고 증가 중 수량이 음수일 경우 예외 테스트")
  void increaseQuantityNegativeQuantity(){
    // given
    Inventory inventory = createTestInventory(createTestProduct("Test Product"), 10);
    when(inventoryRepository.findByInventoryId(1L)).thenReturn(inventory);

    // when
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      inventoryService.increaseQuantity(1L, -10);
    });

    // then
    assertEquals(ErrorCode.NEGATIVE_QUANTITY,exception.getErrorCode());
  }

  @Test
  @DisplayName("재고 감소 중 수량이 음수일 경우 예외 테스트")
  void reduceQuantityNegativeQuantity(){
    Inventory inventory = createTestInventory(createTestProduct("Test Product"), 10);
    when(inventoryRepository.findByProductId(1L)).thenReturn(inventory);

    BusinessException exception = assertThrows(BusinessException.class, () -> {
      inventoryService.reduceQuantity(1L, -10);
    });
    assertEquals(ErrorCode.NEGATIVE_QUANTITY,exception.getErrorCode());
  }

  // 공통 메서드: 공통적인 Product 객체 생성
  private Product createTestProduct(String productName) {
    return Product.builder().productName(productName).build();
  }

  // 공통 메서드: 공통적인 Inventory 객체 생성
  private Inventory createTestInventory(Product product, int quantity) {
    return Inventory.builder().product(product).quantity(quantity).build();
  }


  }