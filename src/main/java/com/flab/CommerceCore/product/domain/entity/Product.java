package com.flab.CommerceCore.product.domain.entity;

import com.flab.CommerceCore.inventory.domain.entity.Inventory;
import com.flab.CommerceCore.order.domain.entity.OrderProduct;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
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



}
