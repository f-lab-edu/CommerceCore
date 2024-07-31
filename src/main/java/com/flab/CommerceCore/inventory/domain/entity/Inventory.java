package com.flab.CommerceCore.inventory.domain.entity;

import com.flab.CommerceCore.product.domain.entity.Product;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;

    private Integer quantity;
    private LocalDateTime lastUpdate;
}
