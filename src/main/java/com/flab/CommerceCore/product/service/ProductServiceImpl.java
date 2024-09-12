package com.flab.CommerceCore.product.service;

import com.flab.CommerceCore.common.Mapper.ProductMapper;
import com.flab.CommerceCore.common.exceptions.BusinessException;
import com.flab.CommerceCore.common.exceptions.ErrorCode;
import com.flab.CommerceCore.inventory.domain.dto.InventoryResponse;
import com.flab.CommerceCore.inventory.service.InventoryService;
import com.flab.CommerceCore.product.domain.dto.ProductRequest;
import com.flab.CommerceCore.product.domain.dto.ProductResponse;
import com.flab.CommerceCore.product.domain.entity.Product;
import com.flab.CommerceCore.product.repository.ProductRepository;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final InventoryService inventoryService;
  private final ProductMapper mapper;

  @Autowired
  public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, InventoryService inventoryService) {
    this.productRepository = productRepository;
    this.mapper = productMapper;
    this.inventoryService = inventoryService;
  }


  /**
   * 새로운 상품을 생성하고, 재고 정보를 업데이트하는 메서드
   *
   * @param productRequest 생성할 상품의 요청 정보
   * @return 생성된 상품에 대한 응답 정보
   * @throws BusinessException 중복된 상품명이 있을 경우 발생
   */
  @Override
  @Transactional
  public ProductResponse createProduct(@Valid ProductRequest productRequest) {
    // 상품 요청을 엔티티로 변환
    Product product = mapper.convertRequestToEntity(productRequest);

    // 중복 상품명 확인
    validateDuplicateProduct(product.getProductName());

    // 상품을 저장
    Product savedProduct = productRepository.save(product);

    // 재고 생성 서비스 호출
    int quantity = inventoryService.createInventory(product, productRequest.getQuantity());

    // 저장된 상품과 재고 정보를 반환
    return mapper.convertEntityToResponse(savedProduct, quantity);
  }


  /**
   * 상품 ID로 상품 정보 조회
   *
   * @param productId 조회할 상품 ID
   * @return 조회된 상품과 재고 수량을 포함한 응답 정보
   * @throws BusinessException 등록되지 않은 상품을 조회할 경우 발생
   */
  @Override
  @Transactional(readOnly = true)
  public ProductResponse findProductById(Long productId) {
    // 상품이 존재하는지 확인하고 조회
    Product findProduct = validateNotNullProduct(productId);

    // 해당 상품의 재고 수량 조회
    int quantity = inventoryService.findQuantityByProductId(productId);

    // 조회한 상품과 재고 정보를 반환
    return mapper.convertEntityToResponse(findProduct,quantity);
  }


  /**
   * 페이징된 모든 상품 목록을 조회
   *
   * @param page 조회할 페이지 번호
   * @return 페이징된 상품 목록 응답 정보
   */
  @Override
  @Transactional(readOnly = true)
  public List<ProductResponse> findAllProducts(int page) {
    // 페이지 요청 생성 (기본 10개씩)
    Pageable pageable = PageRequest.of(page, 10);

    // 페이지에 해당하는 상품 목록 조회
    Page<Product> productPage = productRepository.findAll(pageable);

    // 각 상품에 대한 응답 객체로 변환
    return productPage.getContent().stream()
        .map(product -> mapper.convertEntityToResponse(
            product, inventoryService.findQuantityByProductId(product.getProductId())))
        .toList();
  }


  /**
   * 상품을 삭제하는 메서드
   *
   * @param productId 삭제할 상품 ID
   * @throws BusinessException 등록되지 않은 상품일 경우 발생
   */
  @Override
  public void deleteProduct(Long productId) {
    // 상품이 존재하는지 확인
    Product product = validateNotNullProduct(productId);

    // 상품 삭제
    productRepository.delete(product);
  }


  /**
   * 상품 정보를 업데이트하는 메서드
   *
   * @param productId 수정할 상품 ID
   * @param productRequest 수정할 상품 정보
   * @return 수정된 상품에 대한 응답 정보
   */
  @Override
  @Transactional
  public ProductResponse updateProduct(Long productId,@Valid ProductRequest productRequest) {
    // 상품이 존재하는지 확인
    Product product = validateNotNullProduct(productId);

    // 상품 정보 업데이트
    Product updateProduct = product.updateProduct(productRequest);

    // 재고 정보 업데이트
    InventoryResponse inventoryResponse = inventoryService.updateQuantity(productId,
        productRequest.getQuantity());

    // 수정된 상품과 재고 정보를 반환
    return mapper.convertEntityToResponse(updateProduct, inventoryResponse.getQuantity());
  }


  /**
   * 상품명 중복 여부를 확인
   *
   * @param productName 확인할 상품명
   * @throws BusinessException 중복된 상품명이 있을 경우 발생
   */
  private void validateDuplicateProduct(String productName) {
    if (productRepository.findByProductName(productName) != null) {
      log.error(ErrorCode.DUPLICATED_PRODUCT.getDetail(), productName);
      throw BusinessException.create(ErrorCode.DUPLICATED_PRODUCT);
    }
  }

  /**
   * 상품 ID로 등록 여부 확인
   *
   * @param productId 확인할 상품 ID
   * @return 상품 ID로 조회한 상품
   * @throws BusinessException 등록되지 않은 상품일 경우 발생
   */
  private Product validateNotNullProduct(Long productId) {
    Product product = productRepository.findByProductId(productId);
    if(product== null) {
      log.error(ErrorCode.PRODUCT_NOT_FOUND.getDetail(),productId);
      throw BusinessException.create(ErrorCode.PRODUCT_NOT_FOUND);
    }else{
      return product;
    }
  }
}
