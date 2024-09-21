package com.flab.CommerceCore.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import com.flab.CommerceCore.common.exceptions.BusinessException;
import com.flab.CommerceCore.common.exceptions.ErrorCode;
import com.flab.CommerceCore.inventory.domain.entity.Inventory;
import com.flab.CommerceCore.inventory.repository.InventoryRepository;
import com.flab.CommerceCore.order.domain.dto.OrderProductRequest;
import com.flab.CommerceCore.order.domain.dto.OrderRequest;
import com.flab.CommerceCore.order.domain.dto.OrderResponse;
import com.flab.CommerceCore.order.domain.entity.Order;
import com.flab.CommerceCore.order.repository.OrderRepository;
import com.flab.CommerceCore.order.service.OrderService;
import com.flab.CommerceCore.product.domain.entity.Product;
import com.flab.CommerceCore.product.repository.ProductRepository;
import com.flab.CommerceCore.user.domain.entity.User;
import com.flab.CommerceCore.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class OrderIntegrationTest {


  @Autowired
  private OrderService orderService;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private InventoryRepository inventoryRepository;


  @Test
  void testCreateOrder_Success() {
    // Given: 유저 생성
    User user = User.builder()
        .name("kim")
        .email("kim@gmail.com")
        .password("1234")
        .address("test-password")
        .phoneNum("test-num")
        .build();

    userRepository.save(user);

    // Given: 상품 및 재고 생성
    Product product1 = productRepository.save(Product.builder().productName("test-product").price(new BigDecimal(1000)).build());
    inventoryRepository.save(Inventory.builder().product(product1).quantity(100).build());

    Product product2 = productRepository.save(Product.builder().productName("test-product2").price(new BigDecimal(500)).build());
    inventoryRepository.save(Inventory.builder().product(product2).quantity(200).build());

    // Given: 주문 요청 생성
    OrderProductRequest orderProductRequest1 = OrderProductRequest.builder()
        .productId(product1.getProductId())
        .quantity(1)
        .build();

    OrderProductRequest orderProductRequest2 = OrderProductRequest.builder()
        .productId(product2.getProductId())
        .quantity(2)
        .build();

    OrderRequest orderRequest = OrderRequest.builder()
        .userId(user.getUserId())
        .orderProductRequests(List.of(orderProductRequest1, orderProductRequest2))
        .build();

    // When: 주문 생성
    OrderResponse orderResponse = orderService.createOrder(orderRequest);

    // Then: 주문 결과 검증
    assertNotNull(orderResponse);
    assertEquals(user.getUserId(), orderResponse.getUserId());
    assertEquals(2, orderResponse.getOrderProductResponses().size());  // 두 개의 주문 상품

    // 총 금액 검증
    BigDecimal expectedTotalAmount = BigDecimal.valueOf(1000).add(BigDecimal.valueOf(500).multiply(BigDecimal.valueOf(2)));
    assertEquals(expectedTotalAmount, orderResponse.getTotalAmount());

    // 주문이 DB에 정상적으로 저장되었는지 확인
    Order savedOrder = orderRepository.findById(orderResponse.getOrderId()).orElse(null);
    assertNotNull(savedOrder);

    Inventory findInventory1 = inventoryRepository.findByProductId(product1.getProductId());
    Inventory findInventory2 = inventoryRepository.findByProductId(product2.getProductId());
    assertEquals(99, findInventory1.getQuantity());
    assertEquals(198, findInventory2.getQuantity());


  }

  @Test
  @DisplayName("재고가 존재하지 않는 상품에 대한 실패 테스트")
  void testCreateOrder_InventoryNotFound() {
    // Given: 유저 생성
    User user = User.builder()
        .name("kim")
        .email("kim@gmail.com")
        .password("1234")
        .address("test-address")
        .phoneNum("test-num")
        .build();
    userRepository.save(user);

    // Given: 상품 생성 (재고는 없음)
    Product product1 = productRepository.save(Product.builder().productName("test-product").price(new BigDecimal(1000)).build());

    // 재고를 생성하지 않음 (재고가 없는 상태)

    // Given: 주문 요청 생성 (재고 없는 상품에 대한 주문 시도)
    OrderProductRequest orderProductRequest1 = OrderProductRequest.builder()
        .productId(product1.getProductId())
        .quantity(1)
        .build();

    OrderRequest orderRequest = OrderRequest.builder()
        .userId(user.getUserId())
        .orderProductRequests(List.of(orderProductRequest1))
        .build();

    // When & Then: 재고가 없는 상품에 대해 예외 발생 확인
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      orderService.createOrder(orderRequest);
    });

    // 예외 메시지 확인
    assertEquals(ErrorCode.INVENTORY_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void testCreateOrder_InsufficientInventory() {
    // Given: 유저 생성
    User user = User.builder()
        .name("kim")
        .email("kim@gmail.com")
        .password("1234")
        .address("test-address")
        .phoneNum("test-num")
        .build();
    userRepository.save(user);

    // Given: 상품 및 재고 생성
    Product product1 = productRepository.save(Product.builder().productName("test-product").price(new BigDecimal(1000)).build());
    inventoryRepository.save(Inventory.builder().product(product1).quantity(1).build()); // 재고가 1개

    // Given: 재고를 초과하는 주문 요청 생성 (재고는 1개인데, 5개 주문 시도)
    OrderProductRequest orderProductRequest1 = OrderProductRequest.builder()
        .productId(product1.getProductId())
        .quantity(5)  // 재고보다 많은 수량
        .build();

    OrderRequest orderRequest = OrderRequest.builder()
        .userId(user.getUserId())
        .orderProductRequests(List.of(orderProductRequest1))
        .build();

    // When & Then: 재고 부족으로 인해 예외 발생 확인
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      orderService.createOrder(orderRequest);
    });

    // 예외 메시지 확인
    assertEquals(ErrorCode.INSUFFICIENT_INVENTORY, exception.getErrorCode());
  }





}
