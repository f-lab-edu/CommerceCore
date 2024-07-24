package com.flab.CommerceCore.order.domain.entity;

import com.flab.CommerceCore.product.domain.entity.Product;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderProductId;

    @OneToMany
    @JoinColumn(name="order_id")
    private Order order;

    @OneToMany
    @JoinColumn(name="product_id")
    private Product product;

    private int quantity;
    private Double price;
    private LocalDateTime create;

}
