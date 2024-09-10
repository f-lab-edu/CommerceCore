package com.flab.CommerceCore.inventory.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.flab.CommerceCore.common.exceptions.BusinessException;
import com.flab.CommerceCore.common.exceptions.ErrorCode;
import com.flab.CommerceCore.common.exceptions.GlobalExceptionHandler;
import com.flab.CommerceCore.inventory.domain.dto.InventoryResponse;
import com.flab.CommerceCore.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {
  @InjectMocks
  private InventoryController inventoryController;

  @Mock
  private InventoryService inventoryService;

  private MockMvc mockMvc;

  @BeforeEach
  public void init() {
    mockMvc = MockMvcBuilders.standaloneSetup(inventoryController)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  @Test
  @DisplayName("정상적으로 재고 수량 업데이트 성공")
  void updateQuantitySuccess() throws Exception {

    // given
    InventoryResponse inventoryResponse = InventoryResponse.builder()
        .inventoryId(1L)
        .quantity(10)
        .build();

    // when
    when(inventoryService.updateQuantity(1L, 10)).thenReturn(inventoryResponse);

    // then
    mockMvc.perform(put("/inventory/1")
            .param("quantity", "10")  // 'quantity'를 쿼리 파라미터로 전달
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.inventoryId").value(1L))
        .andExpect(jsonPath("$.quantity").value(10));  // 응답 본문의 수량 값 검증

    verify(inventoryService, times(1)).updateQuantity(1L, 10);
  }


  @Test
  @DisplayName("재고 업데이트 시 필수 필드 누락 시 BadRequest 반환")
  void updateQuantityWithMissingFields() throws Exception {

    mockMvc.perform(put("/inventory/1")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());  // 필수 필드가 없을 때 400 Bad Request

    verify(inventoryService, times(0)).updateQuantity(1L,10);  // 서비스 호출되지 않음
  }

  @Test
  @DisplayName("정상적으로 재고 수량 증가 성공")
  void increaseQuantitySuccess() throws Exception {

    // given
    InventoryResponse inventoryResponse = InventoryResponse.builder()
        .inventoryId(1L)
        .quantity(20)  // 기존 수량에 10을 더해 20으로 업데이트
        .build();

    // when
    when(inventoryService.increaseQuantity(1L, 10)).thenReturn(inventoryResponse);

    // then
    mockMvc.perform(patch("/inventory/1/increase")
            .param("quantity", "10")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.inventoryId").value(1L))
        .andExpect(jsonPath("$.quantity").value(20));

    verify(inventoryService, times(1)).increaseQuantity(1L, 10);
  }

  @Test
  @DisplayName("재고 증가 시 필수 필드 누락으로 실패")
  void increaseQuantityWithMissingFields() throws Exception {

    // 필수 필드인 quantity가 누락되었을 때
    mockMvc.perform(patch("/inventory/1/increase")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());  // 400 Bad Request

    verify(inventoryService, times(0)).increaseQuantity(any(Long.class), any(Integer.class));
  }

  @Test
  @DisplayName("재고 수량 업데이트 시 상품을 찾지 못할 경우 테스트")
  void updateQuantityNotFoundProduct() throws Exception {

    // given
    when(inventoryService.updateQuantity(1L, 10))
        .thenThrow(new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

    // then
    mockMvc.perform(put("/inventory/1")
            .param("quantity", "10")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())  // 400 Bad Request 응답
        .andExpect(jsonPath("$.message").value(ErrorCode.PRODUCT_NOT_FOUND.getDetail())); // 에러 메시지 검증

    verify(inventoryService, times(1)).updateQuantity(1L, 10);
  }

  @Test
  @DisplayName("재고 수량 업데이트 시 입력 수량이 음수일 경우 테스트")
  void updateQuantityNegativeQuantity() throws Exception {
    // given
    when(inventoryService.updateQuantity(1L, -10))
        .thenThrow(new BusinessException(ErrorCode.NEGATIVE_QUANTITY)); // 잘못된 수량 예외 발생

    // then
    mockMvc.perform(put("/inventory/1")
            .param("quantity", "-10") // 잘못된 수량 입력
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())  // 잘못된 입력일 경우 400 응답
        .andExpect(jsonPath("$.message").value(ErrorCode.NEGATIVE_QUANTITY.getDetail())); // 에러 메시지 검증

    verify(inventoryService, times(1)).updateQuantity(1L, -10);
  }

  @Test
  @DisplayName("재고 수량 증가 시 재고를 찾지 못할 경우 테스트")
  void increaseQuantityNotFoundInventory() throws Exception {
    // given
    when(inventoryService.increaseQuantity(1L, 10))
        .thenThrow(new BusinessException(ErrorCode.INVENTORY_NOT_FOUND));

    // then
    mockMvc.perform(patch("/inventory/1/increase")
            .param("quantity", "10")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value(ErrorCode.INVENTORY_NOT_FOUND.getDetail())); // 에러 메시지 검증

    verify(inventoryService, times(1)).increaseQuantity(1L, 10);

  }

  @Test
  @DisplayName("재고 수량 증가 시 입력 수량이 음수일 경우 테스트")
  void increaseQuantityInvalidInput() throws Exception {

    // given
    when(inventoryService.increaseQuantity(1L, -10))
        .thenThrow(new BusinessException(ErrorCode.NEGATIVE_QUANTITY));  // 잘못된 수량 예외 발생

    // then
    mockMvc.perform(patch("/inventory/1/increase")
            .param("quantity", "-10")  // 잘못된 수량 입력
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())  // 잘못된 입력일 경우 400 응답
        .andExpect(jsonPath("$.message").value(ErrorCode.NEGATIVE_QUANTITY.getDetail())); // 에러 메시지 검증

    verify(inventoryService, times(1)).increaseQuantity(1L, -10);
  }




}



