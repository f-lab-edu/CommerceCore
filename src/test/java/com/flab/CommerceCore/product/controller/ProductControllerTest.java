package com.flab.CommerceCore.product.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.flab.CommerceCore.common.exceptions.GlobalExceptionHandler;
import com.flab.CommerceCore.product.domain.dto.ProductRequest;
import com.flab.CommerceCore.product.domain.dto.ProductResponse;
import com.flab.CommerceCore.product.service.ProductService;
import com.google.gson.Gson;
import java.math.BigDecimal;
import java.util.Collections;
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
class ProductControllerTest {

  @InjectMocks
  private ProductController productController;

  @Mock
  private ProductService productService;

  private MockMvc mockMvc;
  private Gson gson;
  private ProductRequest productRequest;
  private ProductResponse productResponse;

  @BeforeEach
  public void init() {
    gson = new Gson();

    mockMvc = MockMvcBuilders.standaloneSetup(productController)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();

    productRequest = ProductRequest.builder()
        .productName("Test Product")
        .description("Test Description")
        .price(BigDecimal.valueOf(1000))
        .quantity(10)
        .build();

    productResponse = ProductResponse.builder()
        .productName("Test Product")
        .description("Test Description")
        .price(BigDecimal.valueOf(1000))
        .build();
  }

  @Test
  @DisplayName("상품 생성 성공")
  void createProductSuccess() throws Exception {
    when(productService.createProduct(any(ProductRequest.class))).thenReturn(productResponse);

    mockMvc.perform(
        post("/product")
            .content(gson.toJson(productRequest))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.productName").value("Test Product"))
        .andExpect(jsonPath("$.description").value("Test Description"))
        .andExpect(jsonPath("$.price").value(1000));

    verify(productService, times(1)).createProduct(any(ProductRequest.class));
  }

  @Test
  @DisplayName("단일 상품 조회 성공")
  void getProductSuccess() throws Exception {
    when(productService.findProductById(eq(1L))).thenReturn(productResponse);

    mockMvc.perform(get("/product/1")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.productName").value("Test Product"))
        .andExpect(jsonPath("$.description").value("Test Description"))
        .andExpect(jsonPath("$.price").value(1000));

    verify(productService, times(1)).findProductById(1L);
  }

  @Test
  @DisplayName("모든 상품 조회 성공")
  void getAllProductsSuccess() throws Exception {
    when(productService.findAllProducts(0)).thenReturn(Collections.singletonList(productResponse));

    mockMvc.perform(get("/product?page=0")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].productName").value("Test Product"))
        .andExpect(jsonPath("$[0].description").value("Test Description"))
        .andExpect(jsonPath("$[0].price").value(1000));

    verify(productService, times(1)).findAllProducts(0);
  }

  @Test
  @DisplayName("상품 삭제 성공")
  void deleteProductSuccess() throws Exception {
    doNothing().when(productService).deleteProduct(1L);

    mockMvc.perform(delete("/product/1")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Product deleted successfully"));

    verify(productService, times(1)).deleteProduct(1L);
  }

  @Test
  @DisplayName("상품 수정 성공")
  void updateProductSuccess() throws Exception {
    when(productService.updateProduct(eq(1L), any(ProductRequest.class))).thenReturn(productResponse);

    mockMvc.perform(put("/product/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(gson.toJson(productRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.productName").value("Test Product"))
        .andExpect(jsonPath("$.description").value("Test Description"))
        .andExpect(jsonPath("$.price").value(1000));

    verify(productService, times(1)).updateProduct(eq(1L), any(ProductRequest.class));
  }

}