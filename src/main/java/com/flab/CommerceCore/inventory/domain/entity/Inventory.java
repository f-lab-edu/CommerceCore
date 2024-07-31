package com.flab.CommerceCore.inventory.domain.entity;

import com.flab.CommerceCore.product.domain.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;
    private LocalDateTime lastUpdate;

    // 테스트용 생성자
    public Inventory(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }
}
