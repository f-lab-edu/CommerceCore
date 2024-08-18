package com.flab.CommerceCore.inventory.domain.entity;

import com.flab.CommerceCore.common.exceptions.BusinessException;
import com.flab.CommerceCore.common.exceptions.ErrorCode;
import com.flab.CommerceCore.product.domain.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Slf4j
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;
    private LocalDateTime lastUpdate;

    @Builder
    public Inventory(Product product, Integer quantity){
        this.product = product;
        this.quantity = quantity;
        this.lastUpdate = LocalDateTime.now();
    }

    // 비즈니스 코드
    public synchronized void reduceQuantity(Integer quantity){
        if(this.quantity >= quantity){
            this.quantity -= quantity;
            this.lastUpdate = LocalDateTime.now();
        }else{
            log.error(ErrorCode.INSUFFICIENT_INVENTORY.getDetail(),this.quantity,quantity);
            throw BusinessException.create(ErrorCode.INSUFFICIENT_INVENTORY);
        }
    }
}
