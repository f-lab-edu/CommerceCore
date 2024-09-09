package com.flab.CommerceCore.product.domain.entity;

import com.flab.CommerceCore.order.domain.entity.OrderProduct;
import com.flab.CommerceCore.product.domain.dto.ProductRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String productName;
    private String description;
    private BigDecimal price;
    private LocalDateTime createTime;

    @OneToMany(mappedBy = "product")
    private List<OrderProduct> orderProducts;

    @Builder
    public Product(String productName, String description, BigDecimal price) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.createTime = LocalDateTime.now();
    }

    public Product updateProduct(ProductRequest productRequest) {
        this.productName = productRequest.getProductName();
        this.description = productRequest.getDescription();
        this.price = productRequest.getPrice();
        return this;
    }
}
