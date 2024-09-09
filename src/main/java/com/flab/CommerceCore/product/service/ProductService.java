package com.flab.CommerceCore.product.service;

import com.flab.CommerceCore.common.exceptions.BusinessException;
import com.flab.CommerceCore.product.domain.dto.ProductRequest;
import com.flab.CommerceCore.product.domain.dto.ProductResponse;
import java.util.List;

public interface ProductService {

  /**
   * 새로운 상품을 생성하고, 재고 정보를 업데이트하는 메서드
   *
   * @param productRequest 생성할 상품의 요청 정보
   * @return 생성된 상품에 대한 응답 정보
   * @throws BusinessException 중복된 상품명이 있을 경우 발생
   */
  ProductResponse createProduct(ProductRequest productRequest);

  /**
   * 상품 ID로 상품 정보 조회
   *
   * @param productId 조회할 상품 ID
   * @return 조회된 상품에 대한 응답 정보
   * @throws BusinessException 등록되지 않은 상품을 조회할 경우 발생
   */
  ProductResponse findProductById(Long productId);

  /**
   * 상품 목록을 페이징 처리하여 조회
   *
   * @param page 조회할 페이지 번호
   * @return 페이징된 상품 목록
   */
  List<ProductResponse> findAllProducts(int page);

  /**
   * 상품을 삭제하는 메서드
   *
   * @param productId 삭제할 상품 ID
   * @throws BusinessException 상품이 존재하지 않을 경우 발생
   */
  void deleteProduct(Long productId);

  /**
   * 상품 정보를 업데이트하는 메서드
   *
   * @param productId 수정할 상품 ID
   * @param productRequest 수정할 상품 정보
   * @return 수정된 상품에 대한 응답 정보
   */
  ProductResponse updateProduct(Long productId, ProductRequest productRequest);
}


