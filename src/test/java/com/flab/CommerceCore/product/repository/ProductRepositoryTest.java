package com.flab.CommerceCore.product.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.flab.CommerceCore.product.domain.dto.ProductRequest;
import com.flab.CommerceCore.product.domain.entity.Product;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

  @Autowired
  ProductRepository productRepository;

  @Test
  void saveAndFindProductByProductId(){
    // given
    Product product = createProduct("사과", "맛있다.", new BigDecimal(1000));

    // when
    Product saveProduct = productRepository.save(product);
    Product findProduct = productRepository.findByProductId(saveProduct.getProductId());

    // then
    assertNotNull(findProduct);
    assertEquals(saveProduct.getProductId(), findProduct.getProductId());
    assertEquals(saveProduct.getProductName(), findProduct.getProductName());
    assertEquals(saveProduct.getDescription(), findProduct.getDescription());
    assertEquals(saveProduct.getPrice(), findProduct.getPrice());
    assertEquals(saveProduct.getCreateTime(), findProduct.getCreateTime());
  }

  @Test
  void findAllProduct(){
    // given
    productRepository.save(createProduct("사과", "빨갛다", new BigDecimal(1000)));
    productRepository.save(createProduct("바나나", "노랗다", new BigDecimal(2000)));
    productRepository.save(createProduct("포도", "푸르다", new BigDecimal(3000)));

    // when
    List<Product> products = productRepository.findAll();

    // then
    assertNotNull(products);
    assertEquals(products.size(), 3);
  }

  @Test
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


  
  private Product createProduct(String name, String description, BigDecimal price) {
    return Product.builder()
        .productName(name)
        .description(description)
        .price(price)
        .build();
  }



}