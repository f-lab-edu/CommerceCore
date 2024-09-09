package com.flab.CommerceCore.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flab.CommerceCore.common.Mapper.ProductMapper;
import com.flab.CommerceCore.common.exceptions.BusinessException;
import com.flab.CommerceCore.common.exceptions.ErrorCode;
import com.flab.CommerceCore.inventory.domain.dto.InventoryResponse;
import com.flab.CommerceCore.inventory.domain.entity.Inventory;
import com.flab.CommerceCore.inventory.repository.InventoryRepository;
import com.flab.CommerceCore.inventory.service.InventoryService;
import com.flab.CommerceCore.product.domain.dto.ProductRequest;
import com.flab.CommerceCore.product.domain.dto.ProductResponse;
import com.flab.CommerceCore.product.domain.entity.Product;
import com.flab.CommerceCore.product.repository.ProductRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @InjectMocks
  ProductServiceImpl productService;

  @Mock
  ProductRepository productRepository;

  @Mock
  InventoryService inventoryService;

  @Mock
  InventoryRepository inventoryRepository;

  @Mock
  ProductMapper mapper;

  private ProductRequest productRequest;
  private Product product;
  private ProductResponse productResponse;


  @BeforeEach
  void setUp() {
    productRequest = ProductRequest.builder()
        .productName("Test Product")
        .description("Test Description")
        .price(BigDecimal.valueOf(1000))
        .quantity(10)
        .build();

    product = Product.builder()
        .productName("Test Product")
        .description("Test Description")
        .price(BigDecimal.valueOf(1000))
        .build();

    productResponse = ProductResponse.builder()
        .productName("Test Product")
        .description("Test Description")
        .price(BigDecimal.valueOf(1000))
        .build();

  }

  @Test
  @DisplayName("중복된 상품일 때 예외가 발생")
  void createProductFailDuplicated() {
    // Given
    when(productRepository.findByProductName(productRequest.getProductName())).thenReturn(product);
    when(mapper.convertRequestToEntity(productRequest)).thenReturn(product);

    // When & Then
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      productService.createProduct(productRequest);
    });

    assertEquals(ErrorCode.DUPLICATED_PRODUCT, exception.getErrorCode());
    verify(productRepository, never()).save(any(Product.class));
  }

  @Test
  @DisplayName("DB 예외 발생 시 예외 발생")
  void createProductFailRepositoryError() {
    when(productRepository.save(any(Product.class))).thenThrow(new DataAccessException("DB ERROR") {});
    when(mapper.convertRequestToEntity(any(ProductRequest.class))).thenReturn(product);

    assertThrows(DataAccessException.class,()->{
      productService.createProduct(productRequest);
    });
  }

  @Test
  @DisplayName("중복되지 않은 상품을 성공적으로 생성")
  void createProductSuccess() {
    // Given
    when(productRepository.findByProductName(productRequest.getProductName())).thenReturn(null);
    when(productRepository.save(any(Product.class))).thenReturn(product);
    when(mapper.convertRequestToEntity(any(ProductRequest.class))).thenReturn(product);
    when(mapper.convertEntityToResponse(product,10)).thenReturn(productResponse);
    when(inventoryService.createInventory(product,10)).thenReturn(10);

    // When
    ProductResponse result = productService.createProduct(productRequest);

    // Then
    assertEquals(productRequest.getProductName(), result.getProductName());
    assertEquals(productRequest.getDescription(), result.getDescription());
    assertEquals(productRequest.getPrice(), result.getPrice());

    verify(productRepository).save(product);
    verify(inventoryService).createInventory(product, productRequest.getQuantity());
  }

  @Test
  @DisplayName("정상적으로 상품 조회")
  void findProductByIdSuccess(){
    // given

    when(productRepository.findByProductId(1L)).thenReturn(product);
    when(mapper.convertEntityToResponse(product,10)).thenReturn(productResponse);
    when(inventoryService.findQuantityByProductId(1L)).thenReturn(10);
    // when

    ProductResponse response = productService.findProductById(1L);
    // then

    assertNotNull(response);
    assertEquals(productResponse.getProductName(), response.getProductName());
    assertEquals(productResponse.getDescription(), response.getDescription());
    assertEquals(productResponse.getPrice(), response.getPrice());

  }


  @Test
  @DisplayName("조회할 상품이 존재하지 않을 때 예외 발생")
  void findProductByIdFailNotFound(){
    when(productRepository.findByProductId(1L)).thenReturn(null);

    assertThrows(BusinessException.class,()->{
      productService.findProductById(1L);
    });

    verify(productRepository, times(1)).findByProductId(1L);
  }

  @Test
  @DisplayName("성공적으로 상품 삭제")
  void deleteProductSuccess() {
    // given
    when(productRepository.findByProductId(1L)).thenReturn(product);

    // when
    productService.deleteProduct(1L);

    // then
    verify(productRepository, times(1)).delete(product);
  }

  @Test
  @DisplayName("삭제할 상품이 존재하지 않을 때 예외 발생")
  void deleteProductFailNotFound() {
    // given
    when(productRepository.findByProductId(1L)).thenReturn(null);

    // when & then
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      productService.deleteProduct(1L);
    });

    assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
    verify(productRepository, never()).delete(any(Product.class));
  }

  @Test
  @DisplayName("성공적으로 상품 업데이트")
  void updateProductSuccess() {
    // given
    InventoryResponse inventoryResponse = InventoryResponse.builder().
        product(product).quantity(10).build();

    when(productRepository.findByProductId(1L)).thenReturn(product);
    when(mapper.convertEntityToResponse(product, 10)).thenReturn(productResponse);
    when(inventoryService.updateQuantity(1L, 10)).thenReturn(inventoryResponse);

    // when
    ProductResponse result = productService.updateProduct(1L, productRequest);

    // then
    assertNotNull(result);
    assertEquals(productRequest.getProductName(), result.getProductName());
    assertEquals(productRequest.getDescription(), result.getDescription());
    assertEquals(productRequest.getPrice(), result.getPrice());

    verify(inventoryService, times(1)).updateQuantity(1L, productRequest.getQuantity());
  }

  @Test
  @DisplayName("업데이트할 상품이 존재하지 않을 때 예외 발생")
  void updateProductFailNotFound() {
    // given
    when(productRepository.findByProductId(1L)).thenReturn(null);

    // when & then
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      productService.updateProduct(1L, productRequest);
    });

    assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
    verify(productRepository, never()).save(any(Product.class));
  }




}