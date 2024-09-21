package com.flab.CommerceCore.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.flab.CommerceCore.common.exceptions.BusinessException;
import com.flab.CommerceCore.common.exceptions.ErrorCode;
import com.flab.CommerceCore.inventory.domain.entity.Inventory;
import com.flab.CommerceCore.inventory.repository.InventoryRepository;
import com.flab.CommerceCore.inventory.service.InventoryService;
import com.flab.CommerceCore.product.domain.dto.ProductRequest;
import com.flab.CommerceCore.product.domain.dto.ProductResponse;
import com.flab.CommerceCore.product.domain.entity.Product;
import com.flab.CommerceCore.product.repository.ProductRepository;
import com.flab.CommerceCore.product.service.ProductService;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class ProductIntegrationTest {

  @Autowired
  private ProductService productService;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private InventoryService inventoryService;

  @Autowired
  private InventoryRepository inventoryRepository;

  @Test
  void createProductWithInventory(){
    // given
    ProductRequest productRequest = ProductRequest.builder()
        .productName("test-product")
        .description("test-description")
        .price(BigDecimal.valueOf(1000))
        .quantity(100)
        .build();

    // when
    ProductResponse productResponse = productService.createProduct(productRequest);

    // then
    assertNotNull(productResponse);
    assertEquals("test-product",productResponse.getProductName());

    assertEquals(100, productResponse.getQuantity());

    Product savedProduct = productRepository.findByProductName(productResponse.getProductName());
    assertNotNull(savedProduct);
    assertEquals("test-product",savedProduct.getProductName());

    Inventory inventory = inventoryRepository.findByProductId(savedProduct.getProductId());
    assertNotNull(inventory);
    assertEquals(100,inventory.getQuantity());

  }

  @Test
  void updateProductWithInventory(){
    // given
    ProductRequest productRequest = ProductRequest.builder()
        .productName("test-product")
        .description("test-description")
        .price(BigDecimal.valueOf(1000))
        .quantity(100)
        .build();

    ProductResponse productResponse = productService.createProduct(productRequest);

    Product findProduct = productRepository.findByProductName(productResponse.getProductName());

    ProductRequest updateRequest = ProductRequest.builder()
        .productName("update-product")
        .description("update-description")
        .price(BigDecimal.valueOf(2000))
        .quantity(200)
        .build();

    // when
    ProductResponse updatedResponse = productService.updateProduct(findProduct.getProductId(),
        updateRequest);

    // then


    assertNotNull(updatedResponse);
    assertEquals("update-product",updatedResponse.getProductName());
    assertEquals(200,updatedResponse.getQuantity());
    assertEquals("update-description",updatedResponse.getDescription());

    Inventory inventory = inventoryRepository.findByProductId(findProduct.getProductId());
    assertNotNull(inventory);
    assertEquals(200,inventory.getQuantity());
  }

  @Test
  void testFindProductById_Success() {
    // Given: 상품과 재고를 먼저 생성
    ProductRequest productRequest = ProductRequest.builder()
        .productName("test-product")
        .description("test-description")
        .price(BigDecimal.valueOf(1000))
        .quantity(100)
        .build();

    ProductResponse productResponse = productService.createProduct(productRequest);
    Product findProduct = productRepository.findByProductName(productResponse.getProductName());

    // When: 생성된 상품을 조회
    ProductResponse response = productService.findProductById(findProduct.getProductId());

    // Then: 조회된 상품과 재고 정보가 올바른지 확인
    assertNotNull(response);
    assertEquals(findProduct.getProductId(), response.getProductId());
    assertEquals("test-product", response.getProductName());
    assertEquals(100, response.getQuantity()); // 재고 수량 확인
    Inventory inventory = inventoryRepository.findByProductId(response.getProductId());
    assertNotNull(inventory);
    assertEquals(100,inventory.getQuantity());
  }

  @Test
  @DisplayName("상품 조회 시 존재하지 않는 상품 처리")
  void testFindProductById_ProductNotFound() {
    // Given: 존재하지 않는 상품 ID
    Long nonExistentProductId = 9999L;

    // When & Then: 존재하지 않는 상품 조회 시 예외 발생 확인
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      productService.findProductById(nonExistentProductId);
    });

    // 예외 메시지 확인
    assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("상품 업데이트 시 상품이 없을 때")
  void testUpdateProduct_ProductNotFound() {
    // Given: 존재하지 않는 상품 ID로 업데이트 요청
    Long nonExistentProductId = 9999L;
    ProductRequest updateRequest = ProductRequest.builder()
        .productName("non-existent-product")
        .description("update-description")
        .price(BigDecimal.valueOf(2000))
        .quantity(200)
        .build();

    // When & Then: 존재하지 않는 상품을 업데이트할 때 예외가 발생하는지 확인
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      productService.updateProduct(nonExistentProductId, updateRequest);
    });

    // 예외 메시지 확인
    assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("상품 생성 시 중복된 상품명 예외")
  void testCreateProduct_DuplicateProductName() {
    // Given: 동일한 이름의 상품이 이미 생성된 상태
    ProductRequest productRequest = ProductRequest.builder()
        .productName("test-product")
        .description("test-description")
        .price(BigDecimal.valueOf(1000))
        .quantity(100)
        .build();
    productService.createProduct(productRequest);  // 첫 번째 상품 생성

    // When & Then: 동일한 이름으로 두 번째 상품 생성 시 예외가 발생하는지 확인
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      productService.createProduct(productRequest);  // 동일한 이름의 상품 생성 시도
    });

    // 예외 메시지 확인
    assertEquals(ErrorCode.DUPLICATED_PRODUCT, exception.getErrorCode());
  }










}
