package com.flab.CommerceCore.product.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.flab.CommerceCore.product.domain.dto.ProductRequest;
import com.flab.CommerceCore.product.domain.entity.Product;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

  @Autowired
  ProductRepository productRepository;

  @Test
  @DisplayName("DB 상품 저장 테스트")
  void saveProduct(){
    // given
    Product product = createProduct("Test-Product", "Test", new BigDecimal(1000));

    // when
    Product saveProduct = productRepository.save(product);

    // then
    assertNotNull(saveProduct);
    assertEquals("Test-Product", saveProduct.getProductName());
  }

  @Test
  @DisplayName("DB 상품 ID로 조회")
  void findByProductId() {
    // given
    Product product = createProduct("Test-Product", "Test", new BigDecimal(1000));
    Product saveProduct = productRepository.save(product);

    // when
    Product findProduct = productRepository.findByProductId(saveProduct.getProductId());

    // then
    assertNotNull(findProduct);
    assertEquals("Test-Product", findProduct.getProductName());
  }

  @Test
  @DisplayName("DB 상품 페이징 처리")
  void findAllProductWithPagination(){
    // given
    for(int i=0; i<15; i++){
      Product product = createProduct("No." + i, i + "번째 상품", new BigDecimal(1000));
      productRepository.save(product);
    }

    // when
    Pageable pageable = PageRequest.of(0, 10); // 페이지 번호 0, 페이지 크기 5
    Page<Product> resultPage = productRepository.findAll(pageable);

    // then
    List<Product> products = resultPage.getContent();
    assertEquals(10, products.size(),"첫 페이지의 상품 수는 5개여야 한다.");
    assertEquals(15, resultPage.getTotalElements(), "전체 상품 수는 15개여야 한다.");
    assertEquals(2, resultPage.getTotalPages(), "전체 페이지 수는 3이어야 한다.");
    assertEquals(0, resultPage.getNumber(), "현재 페이지 번호는 0이어야 한다.");
  }

  @Test
  @DisplayName("DB 상품 업데이트")
  void updateProduct(){
    // given
    Product product = createProduct("사과", "맛있다.", new BigDecimal(1000));
    ProductRequest updateProductRequest = ProductRequest.builder().productName("포도").description("비싸다")
        .price(new BigDecimal(2000)).build();

    Product saveProduct = productRepository.save(product);

    // when
    saveProduct.updateProduct(updateProductRequest);
    Product findProduct = productRepository.findByProductId(saveProduct.getProductId());

    // then
    assertEquals(saveProduct.getProductName(), findProduct.getProductName());
    assertEquals(saveProduct.getDescription(), findProduct.getDescription());
    assertEquals(saveProduct.getPrice(), findProduct.getPrice());
    assertEquals(saveProduct.getCreateTime(), findProduct.getCreateTime());
  }

  @Test
  @DisplayName("DB 상품 삭제")
  void deleteProduct() {
    Product product = createProduct("Test-Product", "Test", new BigDecimal(1000));

    Product savedProduct = productRepository.save(product);

    productRepository.delete(savedProduct);

    Product findProduct = productRepository.findByProductId(savedProduct.getProductId());

    assertNull(findProduct);
  }

  private Product createProduct(String name, String description, BigDecimal price) {
    return Product.builder()
        .productName(name)
        .description(description)
        .price(price)
        .build();
  }



}