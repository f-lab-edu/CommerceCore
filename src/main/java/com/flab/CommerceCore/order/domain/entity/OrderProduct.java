package com.flab.CommerceCore.order.domain.entity;

import com.flab.CommerceCore.product.domain.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    // 생성 메서드
    public static OrderProduct OrderProduct(Product product, int quantity){
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.product = product;
        orderProduct.quantity = quantity;
        orderProduct.calculateTotalPrice();
        return orderProduct;
    }


    // 비즈니스 코드
    public void calculateTotalPrice(){
        this.totalPrice = this.product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

}
