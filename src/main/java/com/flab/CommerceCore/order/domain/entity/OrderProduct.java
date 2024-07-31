package com.flab.CommerceCore.order.domain.entity;

import com.flab.CommerceCore.product.domain.entity.Product;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderProductId;

    @ManyToOne
    @JoinColumn(name="order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;

    private int quantity;

    private BigDecimal totalPrice;

    private LocalDateTime createOrderProduct;

}
