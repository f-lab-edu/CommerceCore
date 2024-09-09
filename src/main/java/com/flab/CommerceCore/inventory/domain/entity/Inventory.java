package com.flab.CommerceCore.inventory.domain.entity;

import com.flab.CommerceCore.common.exceptions.BusinessException;
import com.flab.CommerceCore.common.exceptions.ErrorCode;
import com.flab.CommerceCore.product.domain.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
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

    @OneToOne(cascade = CascadeType.ALL)
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


    /**
     * 재고 감소 메서드
     *
     * @param quantity 증가시킬 재고 수량
     * @throws BusinessException 요청 수량이 남은 재고보다 클 경우 또는 음수일 경우
     */
    public synchronized void reduceQuantity(Integer quantity){
        if(this.quantity >= quantity){
            validateQuantity(quantity);
            this.quantity -= quantity;
            this.lastUpdate = LocalDateTime.now();
            log.info("재고 감소 : {}에서 {}개로 ",this.quantity+quantity, this.product);
        }else{
            log.error(ErrorCode.INSUFFICIENT_INVENTORY.getDetail(),this.quantity,quantity);
            throw BusinessException.create(ErrorCode.INSUFFICIENT_INVENTORY);
        }
    }


    /**
     * 재고 증가 메서드
     *
     * @param quantity 증가 시킬 재고 수량
     * @throws BusinessException 요청 수량이 음수일 경우
     */
    public synchronized void increaseQuantity(Integer quantity){
        validateQuantity(quantity);
        this.quantity += quantity;
        this.lastUpdate = LocalDateTime.now();
        log.info("재고 증가 : {}에서 {}개로",this.quantity-quantity, this.product);
    }


    /**
     * 재고 수정 메서드
     *
     * @param quantity 수정할 재고 수량
     * @throws BusinessException 요청 수량이 음수일 경우
     */
    public synchronized void updateQuantity(Integer quantity){
        validateQuantity(quantity);
        this.quantity = quantity;
        this.lastUpdate = LocalDateTime.now();
    }


    /**
     * 재고가 음수인지 검사하는 메서드
     *
     * @param quantity 검사할 수량
     * @throws BusinessException 수량이 음수일경우
     */
    private void validateQuantity(Integer quantity){
        if(quantity < 0){
            log.error(ErrorCode.NEGATIVE_QUANTITY.getDetail(),quantity);
            throw BusinessException.create(ErrorCode.NEGATIVE_QUANTITY);
        }
    }

}
