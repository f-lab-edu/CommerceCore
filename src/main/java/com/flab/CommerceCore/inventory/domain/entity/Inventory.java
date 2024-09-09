package com.flab.CommerceCore.inventory.domain.entity;

import com.flab.CommerceCore.common.enums.InventoryOperation;
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
    }

    @PrePersist
    @PreUpdate
    void setLastUpdate(){
        this.lastUpdate = LocalDateTime.now();
    }

    /**
     * 재고 수량을 수정하는 메서드
     * 재고를 증가시키거나 감소시키거나, 특정 값으로 재설정할 수 있다.
     *
     * @param quantity 수정할 재고 수량 (증가 또는 감소할 수량)
     * @param operation 수행할 재고 작업 유형 (INCREASE: 재고 증가, DECREASE: 재고 감소, UPDATE: 재고 수량 수정)
     *
     * @throws BusinessException 재고 감소 시, 요청한 수량이 현재 재고보다 클 경우 발생
     * @throws IllegalArgumentException 잘못된 operation 이 전달되었을 경우 발생
     */
    public synchronized void modifyQuantity(Integer quantity, InventoryOperation operation){
        validateQuantity(quantity);

        switch (operation){
            case INCREASE -> {
                this.quantity += quantity;
                log.info("재고 증가 : {}에서 {}개로",this.quantity-quantity, this.quantity);
            }
            case DECREASE -> {
                if(this.quantity >= quantity){
                    this.quantity -= quantity;
                    log.info("재고 감소 : {}에서 {}개로 ",this.quantity+quantity, this.quantity);
                } else {
                    log.error(ErrorCode.INSUFFICIENT_INVENTORY.getDetail(),this.quantity,quantity);
                    throw BusinessException.create(ErrorCode.INSUFFICIENT_INVENTORY);
                }
            }
            case UPDATE -> {
                this.quantity = quantity;
                log.info("재고 수정 : 새로운 재고 수량 {}", this.quantity);
            }
            default -> throw new IllegalArgumentException("잘못된 입력 : " + operation);
        }

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
