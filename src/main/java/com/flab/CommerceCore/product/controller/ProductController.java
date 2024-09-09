package com.flab.CommerceCore.product.controller;

import com.flab.CommerceCore.product.domain.dto.ProductRequest;
import com.flab.CommerceCore.product.domain.dto.ProductResponse;
import com.flab.CommerceCore.product.service.ProductService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {

  private final ProductService productService;

  @Autowired
  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  /**
   * 상품 생성 API
   *
   * @param productRequest 생성할 상품 정보
   * @return 생성된 상품 정보
   */
  @PostMapping
  public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest) {
    ProductResponse productResponse = productService.createProduct(productRequest);
    return ResponseEntity.status(201).body(productResponse);
  }

  /**
   * 단일 상품 조회 API
   *
   * @param productId 조회할 상품 ID
   * @return 조회된 상품 정보
   */
  @GetMapping("/{productId}")
  public ResponseEntity<ProductResponse> getProduct(@PathVariable("productId") Long productId) {
    ProductResponse productResponse = productService.findProductById(productId);
    return ResponseEntity.ok(productResponse);
  }

  /**
   * 페이징 처리된 모든 상품 조회 API
   *
   * @param page 조회할 페이지 번호
   * @return 조회된 상품들 정보
   */
  @GetMapping
  public ResponseEntity<List<ProductResponse>> getAllProducts(@RequestParam(defaultValue = "0", name = "page") int page) {
    List<ProductResponse> productResponseList = productService.findAllProducts(page);
    return ResponseEntity.ok(productResponseList);

  }

  /**
   * 상품 삭제 API
   *
   * @param productId 삭제할 상품 ID
   * @return 삭제 완료 메시지
   */
  @DeleteMapping("/{productId}")
  public ResponseEntity<String> deleteProduct(@PathVariable("productId") Long productId) {
    productService.deleteProduct(productId);
    return ResponseEntity.ok("Product deleted successfully");
  }

  /**
   * 상품 수정 API
   * @param productRequest 수정된 정보가 담긴 객체
   * @param productId 수정할 상품 ID
   * @return 수정이 완료된 상품 정보
   */
  @PutMapping("/{productId}")
  public ResponseEntity<ProductResponse> updateProduct(@PathVariable("productId") Long productId,
      @RequestBody ProductRequest productRequest) {
    ProductResponse productResponse = productService.updateProduct(productId, productRequest);
    return ResponseEntity.ok(productResponse);
  }

}
