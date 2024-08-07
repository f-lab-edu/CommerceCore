package com.flab.CommerceCore.order.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
public class OrderProductResponse {
    private Long productId;
    private String productName;
    private BigDecimal totalPrice;
    private int quantity;
}
